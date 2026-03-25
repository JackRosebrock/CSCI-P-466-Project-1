package com.example.Project1.util;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class YouTubeUtil 
{

    private YouTubeUtil() {}

    // Function to get the video ID from a YouTube URL
    public static String extractVideoId(String url) 
    {
        if (url == null || url.isBlank()) 
        {
            throw new IllegalArgumentException("Video URL is required.");
        }

        String trimmedUrl = url.trim();

        try 
        {
            URI uri = URI.create(trimmedUrl);
            String host = Objects.toString(uri.getHost(), "").toLowerCase();
            String path = Objects.toString(uri.getPath(), "");

            // If the format is youtu.be/<id>
            if (host.contains("youtu.be")) 
            {
            String candidate = path.startsWith("/") ? path.substring(1) : path;
                return stripAfterDelimiters(candidate);
            }


            // If the format is youtube.com/embed/<id>
            if (path.startsWith("/embed/")) 
            {
                return stripAfterDelimiters(path.substring("/embed/".length()));
            }


            // If the format is youtube.com/watch?v=<id>
            Map<String, String> query = parseQuery(uri.getRawQuery());
            
            if (query.containsKey("v")) 
            {
                return stripAfterDelimiters(query.get("v"));
            }
        } 

        catch (IllegalArgumentException ignored) 
        {
            // Let the argument fall through to the fallback
        }

        // fallback: find "v=" in the url
        int vIndex = trimmedUrl.indexOf("v=");
        if (vIndex >= 0) {
            return stripAfterDelimiters(trimmedUrl.substring(vIndex + 2));
        }

        throw new IllegalArgumentException("Could not extract YouTube video id from URL: " + trimmedUrl);
    }

    // Helper function to get rid of any query parameters or fragments from the video ID
    private static String stripAfterDelimiters(String s) 
    {
        if (s == null) return "";
        
        String out = s;

        for (char delim : new char[]{'?', '&', '#', '/'}) 
        {
            int idx = out.indexOf(delim);
            if (idx >= 0) out = out.substring(0, idx);
        }

        return out;
    }

    private static Map<String, String> parseQuery(String rawQuery) 
    {
        Map<String, String> map = new HashMap<>();

        if (rawQuery == null || rawQuery.isBlank()) return map;

        for (String pair : rawQuery.split("&")) 
        {
            int eq = pair.indexOf('=');

            if (eq <= 0) continue;

            String key = URLDecoder.decode(pair.substring(0, eq), StandardCharsets.UTF_8);
            String val = URLDecoder.decode(pair.substring(eq + 1), StandardCharsets.UTF_8);

            map.put(key, val);
        }
        
        return map;
    }
}