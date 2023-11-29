package com.peqa;

import static spark.Spark.*;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;


class Notas {
    public ArrayList<Double> notas = new ArrayList<Double>();

    public void add(double nota) {
        notas.add(nota);
    }

    public JSONArray all() {
        JSONArray json = new JSONArray();
        for(int i = 0; i < notas.size(); i++) {
            JSONObject data = new JSONObject();
            data.put("id", i);
            data.put("value", notas.get(i));
            json.put(data);
        }
        return json;
    }

    public double average() {
        if (notas.size() == 0) {
            return 0;
        }
        double suma = 0;
        for (int i = 0; i < all().length(); i++) {
            suma += notas.get(i);
        }
        return suma / notas.size();
    }
}



public class App {

    public static void main(String[] args) {

        Notas notas = new Notas();
        System.out.println("Executing on port: 4567");
        enableCors();
        get("/notas", (req, res) -> {
            res.type("application/json");
            JSONObject json = new JSONObject();
            json.put("data", notas.all());
            json.put("average", notas.average());
            return json.toString();
        });
        get("/notas/:id", (req, res) -> { // validar que id sea int
            res.type("application/json");
            String rawId = req.params("id");
            if (!rawId.matches("\\d+")) {
                return "El id debe ser un número";
            }
            int id = Integer.parseInt(rawId);
            if (id >= notas.all().length()) {
                return "No existe la nota";
            }
            return notas.all().get(id).toString();
        });
        post("/notas", (req, res) -> {
            res.type("application/json");
            JSONObject json = new JSONObject(req.body());
            double nota = Double.parseDouble(json.getString("nota"));
            notas.add(nota);
            return "Nota agregada";
        });
    }
        
    private static void enableCors() {
        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
    
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
    
            return "OK";
        });
    
        before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));
    }
}