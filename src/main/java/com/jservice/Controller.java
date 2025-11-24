package com.jservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;


@RestController
public class Controller {

    @GetMapping(value = "/home", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> page() {
        String html = """
            <!DOCTYPE html>
            <html lang="en">
              <head>
                <meta charset="UTF-8">
                <title>Hello Nihal</title>
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <style>
                  body { font-family: system-ui, sans-serif; margin: 2rem; }
                  .card { max-width: 640px; border: 1px solid #ddd; border-radius: 8px; padding: 1.5rem; }
                  h1 { margin-top: 0; }
                  .btn { display: inline-block; margin-top: 1rem; padding: 0.5rem 1rem; background: #2d6cdf; color: #fff; border-radius: 6px; text-decoration: none; }
                  .btn:hover { background: #1f4fb0; }
                </style>
              </head>
              <body>
                <div class="card">
                  <h1>Hello Nihal</h1>
                  <p>This is a simple HTML page returned by a RestController.</p>
                  <a class="btn" href="/actuator/health">Health</a>
                </div>
              </body>
            </html>
            """;
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
    }
}
