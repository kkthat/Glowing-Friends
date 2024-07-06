package com.glowingfriends.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetUUID {

    public static String getUUID(String username) {
        try {
            // Create the URL with the username.
            String urlString = "https://api.mojang.com/users/profiles/minecraft/" + username;
            URL url = new URL(urlString);

            // Open connection to the URL.
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Get the response code.
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response.
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse the JSON response.
                String jsonResponse = response.toString().trim();
                //System.out.println("JSON Response: " + jsonResponse); // Debugging statement

                // Deserialize JSON response
                String id = null;
                String name = null;

                // Remove whitespace and newline characters
                jsonResponse = jsonResponse.replaceAll("\\s+", "");

                // Check if the JSON response is not empty and contains the expected fields
                if (jsonResponse.startsWith("{") && jsonResponse.endsWith("}")) {
                    // Split by commas to separate key-value pairs
                    String[] pairs = jsonResponse.substring(1, jsonResponse.length() - 1).split(",");
                    for (String pair : pairs) {
                        // Split each pair into key and value
                        String[] keyValue = pair.split(":");
                        if (keyValue.length == 2) {
                            String key = keyValue[0].replaceAll("\"", "");
                            String value = keyValue[1].replaceAll("\"", "");
                            if (key.equals("id")) {
                                id = value;
                            } else if (key.equals("name")) {
                                name = value;
                            }
                        }
                    }
                }

                if (id != null) {
                    // Format the UUID.
                    String formattedUUID = formatUUID(id);
                    return formattedUUID;
                } else {
                    //System.out.println("Failed to extract UUID from JSON for username: " + username);
                }

            } else {
                //System.out.println("GET request failed. Response code: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // Return null if there's an error
    }

    // Format the response from trimmed to full. i.e. add hyphens.
    public static String formatUUID(String trimmedUUID) {
        return trimmedUUID.replaceFirst(
                "([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{12})",
                "$1-$2-$3-$4-$5"
        );
    }
}