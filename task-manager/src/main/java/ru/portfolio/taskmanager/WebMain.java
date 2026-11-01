package ru.portfolio.taskmanager;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import ru.portfolio.taskmanager.model.*;
import ru.portfolio.taskmanager.service.TaskService;
import ru.portfolio.taskmanager.repository.TaskRepository;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/** Local web adapter for the same service and data file as the console app. */
public class WebMain {
    private static TaskService service;
    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        service = new TaskService(new TaskRepository());
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
        server.createContext("/", WebMain::handle);
        server.start();
        System.out.println("Task Manager: http://127.0.0.1:" + port + " (Ctrl+C to stop)");
    }
    private static void handle(HttpExchange x) throws IOException {
        try {
            String path = x.getRequestURI().getPath();
            if (path.equals("/api/tasks")) {
                if (x.getRequestMethod().equals("POST")) {
                    String origin = x.getRequestHeaders().getFirst("Origin");
                    if (origin != null && !origin.equals("http://" + x.getRequestHeaders().getFirst("Host"))) {
                        reply(x, 403, "application/json", "{\"error\":\"Forbidden origin\"}"); return;
                    }
                    byte[] body = x.getRequestBody().readNBytes(65537);
                    if (body.length > 65536) throw new IllegalArgumentException("Слишком большой запрос");
                    Map<String,String> form = new HashMap<>();
                    for (String part : new String(body, StandardCharsets.UTF_8).split("&")) {
                        String[] kv = part.split("=", 2);
                        if (kv.length == 2) form.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8), URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
                    }
                    switch (form.getOrDefault("action", "")) {
                        case "create" -> {
                            String title = form.getOrDefault("title", "");
                            String description = form.getOrDefault("description", "");
                            if (title.length() > 200 || description.length() > 5000) throw new IllegalArgumentException("Слишком длинный текст");
                            service.addTask(title, description, Priority.valueOf(form.getOrDefault("priority", "MEDIUM")));
                        }
                        case "status" -> service.updateStatus(Integer.parseInt(form.get("id")), TaskStatus.valueOf(form.get("status")));
                        case "delete" -> service.deleteTask(Integer.parseInt(form.get("id")));
                        default -> throw new IllegalArgumentException("Неизвестное действие");
                    }
                } else if (!x.getRequestMethod().equals("GET")) {
                    reply(x, 405, "application/json", "{}"); return;
                }
                String json = service.getAllTasks().stream().map(t -> "{\"id\":"+t.getId()+",\"title\":"+quote(t.getTitle())+",\"description\":"+quote(t.getDescription())+",\"priority\":"+quote(t.getPriority().name())+",\"status\":"+quote(t.getStatus().name())+"}").collect(Collectors.joining(",", "[", "]"));
                reply(x, 200, "application/json", json);
            } else {
                if (!x.getRequestMethod().equals("GET")) { reply(x,405,"text/plain","Method not allowed"); return; }
                String file = switch(path) { case "/", "/index.html" -> "index.html"; case "/app.js" -> "app.js"; case "/style.css" -> "style.css"; default -> null; };
                if (file == null) { reply(x,404,"text/plain","Not found"); return; }
                String type = file.endsWith("js") ? "text/javascript" : file.endsWith("css") ? "text/css" : "text/html";
                reply(x,200,type, Files.readString(Path.of("web",file)));
            }
        } catch (IllegalArgumentException e) {
            reply(x,400,"application/json","{\"error\":"+quote(e.getMessage())+"}");
        } catch (Exception e) {
            e.printStackTrace(); reply(x,500,"application/json","{\"error\":\"Не удалось прочитать или сохранить данные\"}");
        } finally { x.close(); }
    }
    private static String quote(String s) {
        StringBuilder b = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            if(c == '"' || c == '\\') b.append('\\').append(c);
            else if(c < 32) b.append(String.format("\\u%04x", (int)c));
            else b.append(c);
        }
        return b.append('"').toString();
    }
    private static void reply(HttpExchange x,int status,String type,String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        x.getResponseHeaders().set("Content-Type",type+"; charset=utf-8");
        x.getResponseHeaders().set("Cache-Control","no-store");
        x.getResponseHeaders().set("X-Content-Type-Options","nosniff");
        x.sendResponseHeaders(status,bytes.length);
        x.getResponseBody().write(bytes);
    }
}
