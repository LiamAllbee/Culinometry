package com.culinometry.database;

import android.content.Context;
import android.icu.text.UFormat;

import com.culinometry.measurement.IngredientMode;
import com.culinometry.measurement.Unit;
import com.culinometry.model.Ingredient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class IngredientSeedLoader {
    private IngredientSeedLoader() {}

    public static List<Ingredient> load(Context context) {
        List<Ingredient> ingredientList = new ArrayList<>();

        try (
            InputStream inputStream = context.getAssets().open("default_ingredients.json");

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)))
        {
                StringBuilder jsonBuilder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    jsonBuilder.append(line);
                }

                JSONArray jsonArray = new JSONArray(jsonBuilder.toString());

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    String name = object.getString("name");

                    IngredientMode mode = IngredientMode.valueOf(object.getString("mode"));

                    String referenceMass = object.isNull("referenceMass")
                            ? null : object.getString("referenceMass");

                    Unit massUnit = object.isNull("massUnit")
                            ? null : Unit.valueOf(object.getString("massUnit"));

                    String referenceVolume = object.isNull("referenceVolume")
                            ? null : object.getString("referenceVolume");

                    Unit volumeUnit = object.isNull("volumeUnit")
                            ? null : Unit.valueOf(object.getString("volumeUnit"));

                    ingredientList.add(new Ingredient(
                            name,
                            referenceMass,
                            massUnit,
                            referenceVolume,
                            volumeUnit,
                            mode,
                            false)
                    );
                }
        }
        catch (IOException | JSONException | IllegalArgumentException exception) {
            throw new IllegalStateException("Failed to load default ingredients", exception);
        }
        return ingredientList;
    }
}
