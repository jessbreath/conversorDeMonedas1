package com.mycompany.conversordemonedas;
/*
    CONVERSOR DE MONEDAS
 */
import com.google.gson.Gson;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Principal {

    //constante que contiene URL del API
    String API_URL = "https://v6.exchangerate-api.com/v6/9446fb6aa71917f5bda4a7c7/latest/USD";
    
    //Realiza solicitudes (servicios web) y su variable
    HttpClient client;
    Map<String, Double> conversionRates;
    
    //Constructor Principal
    public Principal() {
        this.client = HttpClient.newHttpClient();
        this.conversionRates = new HashMap<>();
    }
    
    public void obtenerTasasDeCambio() throws IOException, InterruptedException {
        HttpRequest request;
        try {
            request = HttpRequest.newBuilder(new URI(API_URL)).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error en la URL de la API: " + e.getMessage());
        }
        //Envia solicitud y recibe respuesta del sevidor
         HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            interpretarTasaDeCambio(response.body());
        } else {
            throw new IOException("Error al obtener tasas de cambio: " + response.statusCode());
        }
    }
    
     private void interpretarTasaDeCambio(String responseBody) {
        // Analizar JSON respuesta and extraer conversion de tasas
        // Utilizar biblioteca Gson
        Gson gson = new Gson();
        Cambio exchangeRates = gson.fromJson(responseBody, Cambio.class);
        this.conversionRates = exchangeRates.getConversion_rates();
    }
    public double convertirMoneda(double amount, String fromCurrency, String toCurrency) {
        // Convert amount from one currency to another
        if (!conversionRates.containsKey(fromCurrency) || !conversionRates.containsKey(toCurrency)) {
            throw new IllegalArgumentException("Moneda no encontrada en las tasas de conversión.");
        }

        double fromRate = conversionRates.get(fromCurrency);
        double toRate = conversionRates.get(toCurrency);

        // Realizar la conversión usando regla de tres simple
        double convertedAmount = (amount / fromRate) * toRate;
        return convertedAmount;
    }
     public static void main(String[] args) {
        Principal app = new Principal();
        Scanner scanner = new Scanner(System.in);

        try {
            app.obtenerTasasDeCambio();

            while (true) {
                // Mostrar menú
                System.out.println("***************************************************");
                System.out.println("Sea bienvenido al conversor de Moneda =]");
                System.out.println("1) Dólar =>> Peso argentino");
                System.out.println("2) Peso argentino =>> Dólar");
                System.out.println("3) Dólar =>> Real brasileño");
                System.out.println("4) Real brasileño =>> Dólar");
                System.out.println("5) Dólar =>> Peso colombiano");
                System.out.println("6) Peso colombiano =>> Dólar");
                System.out.println("7) Salir");
                System.out.println("Elija una opción válida: ");
                System.out.println("****************************************************");

                // Leer opción del usuario
                int option = scanner.nextInt();
                scanner.nextLine(); // Consumir el salto de línea después de nextInt()

                if (option == 7) {
                    System.out.println("Salir");
                    break;
                }

                String fromCurrency, toCurrency;
                double amount;

                switch (option) {
                    case 1:
                        fromCurrency = "USD";
                        toCurrency = "ARS";
                        break;
                    case 2:
                        fromCurrency = "ARS";
                        toCurrency = "USD";
                        break;
                    case 3:
                        fromCurrency = "USD";
                        toCurrency = "BRL";
                        break;
                    case 4:
                        fromCurrency = "BRL";
                        toCurrency = "USD";
                        break;
                    case 5:
                        fromCurrency = "USD";
                        toCurrency = "COP";
                        break;
                    case 6:
                        fromCurrency = "COP";
                        toCurrency = "USD";
                        break;
                    default:
                        System.out.println("Opción inválida, elija nuevamente.");
                        continue;
                }

                // Solicitar el monto a convertir
                System.out.println("Ingrese el monto que desea convertir:");
                amount = scanner.nextDouble();
                scanner.nextLine(); // Consumir el salto de línea después de nextDouble()

                // Realizar la conversión
                try {
                    double convertedAmount = app.convertirMoneda(amount, fromCurrency, toCurrency);
                    System.out.printf("El valor %.2f %s corresponde a %.2f %s.%n",
                            amount, fromCurrency, convertedAmount, toCurrency);
                } catch (IllegalArgumentException e) {
                    System.out.println("Error al convertir la moneda: " + e.getMessage());
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
     
}