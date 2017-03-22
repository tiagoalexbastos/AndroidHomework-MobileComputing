package pt.ua.cm.tiagoalexbastos.homework_1_ementas;

import android.os.StrictMode;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by tiagoalexbastos on 02-03-2017.
 */

public class EmentasUAParser {

    private static final String TERMINATOR = "\n\n\n\n";
    public static final String ENCERRADO = "ENCERRADO";

    private static final String CANTINA = "Refeitório: ";
    private static final String SOPA = "Sopa: ";
    private static final String CARNE = "Prato Carne: ";
    private static final String PEIXE = "Prato Peixe: ";
    private static final String VEGETARIANO = "Prato Vegetariano: ";
    private static final String DIETA = "Prato Dieta: ";
    public static final String ENTRADAS = "Buffet de Entradas: ";

    private static final String LOG_V = "LOG_V";
    static String[] meals;


    public static String callOpenMeals() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String mealsJsonStr = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are available at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            URL url = new URL("http://services.web.ua.pt/sas/ementas?date=week&format=json");

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                mealsJsonStr = null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                mealsJsonStr = null;
            }
            mealsJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            mealsJsonStr = null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }
        return mealsJsonStr;
    }

    private static String getReadableDateString(long time){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(time);
    }


    public static  String[] getMealDataFromJson(String mealsJsonStr) throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST1 = "menus";
        final String OWM_LIST2 = "menu";
        final String OWM_ATTRIBUTES = "@attributes";
        final String OWM_CANTEEN = "canteen";
        final String OWM_MEAL = "meal";
        final String OWM_DATE = "date";
        final String OWM_WEEKDAY = "weekday";
        final String OWM_WEEKDAYNR = "weekdayNr";
        final String OWM_DESCRIPTION = "main";

        SimpleDateFormat dataFormater = new SimpleDateFormat("EEE, dd MMM", Locale.getDefault());
        SimpleDateFormat dataParser = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.US);
        Date data = null;
        String data2 = null;

        JSONObject mealsJson = new JSONObject(mealsJsonStr);
        JSONObject mealsArray1 = mealsJson.getJSONObject(OWM_LIST1);
        JSONArray mealsArray = mealsArray1.getJSONArray(OWM_LIST2);


        String[] resultStrs = new String[mealsArray.length()];
        meals = new String[mealsArray.length()];

        for(int i = 0; i < mealsArray.length(); i++) {

            // Get the JSON object representing the day
            JSONObject dayMeal = mealsArray.getJSONObject(i);
            try {
                data = dataParser.parse(dayMeal.getJSONObject(OWM_ATTRIBUTES).getString("date"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String cantina = dayMeal.getJSONObject(OWM_ATTRIBUTES).getString("canteen");
            String tipo = dayMeal.getJSONObject(OWM_ATTRIBUTES).getString("meal");

            data2 = dataFormater.format(data);

            resultStrs[i] = data2 + " - " + cantina + " - " + tipo;
            if(dayMeal.getJSONObject(OWM_ATTRIBUTES).getString("disabled").equals("0")) {

                JSONArray elementosEmenta = dayMeal.getJSONObject("items").getJSONArray("item");
                // get the several meals in a canteen, in a day
                String sopa = parseForObjectOrString(elementosEmenta, 0);
                String carne = parseForObjectOrString(elementosEmenta, 1);
                String peixe = parseForObjectOrString(elementosEmenta, 2);
                String vegetariano = parseForObjectOrString(elementosEmenta, 3);
                String dieta = parseForObjectOrString(elementosEmenta, 4);

                meals[i] = SOPA + sopa + TERMINATOR +
                        CARNE + carne + TERMINATOR +
                        PEIXE + peixe + TERMINATOR +
                        VEGETARIANO + vegetariano + TERMINATOR +
                        DIETA + dieta + TERMINATOR;
            }
            else
                meals[i] = ENCERRADO;
        }


        for (String s : resultStrs) {
            Log.v(LOG_V, "Meal entry: " + s);
        }

        return resultStrs;
    }

    public static String getMeal(int idx)
    {
        return meals[idx];
    }

    private static  String parseForObjectOrString(JSONArray array, int index) throws JSONException {
        JSONObject tempJsonObject = array.optJSONObject(index);
        if( null == tempJsonObject ) {
            // no json object, treat as string
            return array.getString(index);
        } else {
            return array.getJSONObject(index).getJSONObject("@attributes").getString("name");
        }
    }

}
