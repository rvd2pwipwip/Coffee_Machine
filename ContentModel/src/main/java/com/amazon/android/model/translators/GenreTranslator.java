/**
 * Copyright 2015-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *     http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazon.android.model.translators;

import android.util.Log;

import com.amazon.android.model.AModelTranslator;
import com.amazon.android.model.content.Content;
import com.amazon.android.model.content.Genre;
import com.amazon.utils.ListUtils;

import java.util.List;

/**
 * This class extends the {@link AModelTranslator} for the {@link Content} class. It provides a way
 * to translate a {link Map} to a {@link Content} object.
 */
public class GenreTranslator extends AModelTranslator<Genre> {

    private static final String TAG = GenreTranslator.class.getSimpleName();

    /**
     * {@inheritDoc}
     *
     * @return A new {@link Content}
     */
    @Override
    public Genre instantiateModel() {

        return new Genre();
    }

    /**
     * @param model The {@link Content} to set the field on.
     * @param field The {@link String} describing what member variable to set.
     * @param value The {@link Object} value to set the member variable.
     * @return True if the value was set, false if there was an error.
     */
    @Override
    public boolean setMemberVariable(Genre model, String field, Object value) {

        if (model == null || field == null || field.isEmpty()) {
            Log.e(TAG, "Input parameters should not be null and field cannot be empty.");
            return false;
        }
        // This allows for some content to have extra values that others might not have.
        if (value == null) {
            Log.w(TAG, "Value for " + field + " was null so not set for Genre, this may be " +
                    "intentional.");
            return true;
        }
        try {
            switch (field) {
                case Genre.M_ID:
                    model.setId(value.toString());
                    break;
                case Genre.M_TITLE:
                    model.setTitle(value.toString());
                    break;
                case Genre.M_CARD_IMAGE_URL:
                    model.setCardImageUrl(value.toString());
                    break;
                default:
                    break;
            }
        } catch (ClassCastException e) {
            Log.e(TAG, "Error casting value to the required type for field " + field, e);
            return false;
        }
        return true;
    }

    /**
     * @param model The {@link Content} model to verify.
     * @return True if the model is valid; false otherwise.
     */
    @Override
    public boolean validateModel(Genre model) {

        try {
            return !model.getTitle().isEmpty() &&
                    !model.getId().isEmpty() &&
                    !model.getCardImageUrl().isEmpty() ;
        }
        catch (NullPointerException e) {
            Log.e(TAG, "Null pointer found during model validation.", e);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {

        return GenreTranslator.class.getSimpleName();
    }
}
