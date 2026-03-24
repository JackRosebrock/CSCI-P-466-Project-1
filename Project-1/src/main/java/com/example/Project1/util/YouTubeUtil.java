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

    public static String extractVideoId(String url)
    {
        if (url == null || url.isBlank())
        {
            throw new IllegalArgumentException("The Video URL is required.");
        }

        String trimmed_Url = url.trim();

        try
        {
            URI uri = URI.create(trimmed_Url);
            
            String host = Objects.toString(uri.getHost(), "").toLowerCase();
            String path = Objects.toString(uri.getPath(), "");

            
            // If the link is formatted as "youtu.be/<id>"
            if (host.contains("youtu.be"))
            {
                String candidate;

                if (path.startsWith("/"))
                {
                    candidate = path.substring(1);
                }

                else
                {
                    candidate = path;
                }
            }


            // If the link is formatted as "youtube.com/embed/<id>
            if (path.startsWith("/embed/"))
            {
                return stripAfterDelimiters(path.substring("/embed/".length()));
            }
            

            // If the link is formatted as youtube.com/watch?v=<id>
            Map<String, String> query = parseQuery(uri.getRawQuery());

            if (query.containsKey("v"))
            {
                return stripAfterDelimiters(query.get("v"));
            }
        }

        catch (IllegalArgumentException ignored)
        {
            // Let the argument fall through
        }

        // If an IllegalArgumentException is raised then try searching using "v="
        int vIndex = trimmed_Url.indexOf("v=");

        if (vIndex >= 0)
        {
            return stripAfterDelimiters(trimmed_Url.substring(vIndex + 2));
        }
        
        // If the video id is still not retrieved 
        throw new IllegalArgumentException("Could not find Youtube video id from URL: " + trimmed_Url);
    }

    
    // Retrieves the YouTube video's id by taking the string after the delimiter
    private static String stripAfterDelimiters(String input)
    {
        if (input == null) return "";


        String output = input;

        for (char delim : new char[]{'?', '&', '#', '/'})
        {
            int index = output.indexOf(delim);

            if (index >= 0) output = output.substring(0, index);
        }

        return output;
    }


    // Navigates through a 
    private static Map<String, String> parseQuery(String rawQuery)
    {
        Map<String, String> map = new HashMap<>();

        if (rawQuery == null) return map;

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
