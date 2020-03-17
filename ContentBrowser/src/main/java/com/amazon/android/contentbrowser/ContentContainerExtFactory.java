package com.amazon.android.contentbrowser;

import com.amazon.android.contentbrowser.metadata.MetadataExtractor;
import com.amazon.android.model.SvodMetadata;
import com.amazon.android.model.content.Content;
import com.amazon.android.model.content.ContentContainer;
import com.amazon.android.model.content.ContentContainerExt;
import com.amazon.android.model.translators.ContentTranslator;
import com.amazon.android.recipe.NoOpRecipeCallbacks;
import com.amazon.android.recipe.Recipe;
import com.amazon.android.utils.PathHelper;
import com.amazon.dynamicparser.DynamicParser;
import com.amazon.dynamicparser.IParser;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;
import java.util.Map;

public class ContentContainerExtFactory {
    private final MetadataExtractor metadataExtractor = new MetadataExtractor();

    public ContentContainerExt create(String containerName, String data, DynamicParser dynamicParser, Recipe recipe, ContentTranslator contentTranslator)
            throws JsonProcessingException, DynamicParser.ParserNotFoundException, IParser.InvalidQueryException,
            IParser.InvalidDataException, PathHelper.MalformedInjectionStringException {

        ContentContainer contentContainer = ContentContainer.newInstance(containerName);
        SvodMetadata metadata;

        List<Map<String, Object>> cookedJson = dynamicParser.parseInput(recipe, data, null);

        for (Map<String, Object> objectMap : cookedJson) {
            Content content = (Content) dynamicParser.translateMapToModel(recipe, new NoOpRecipeCallbacks(), objectMap);

            if (contentTranslator.validateModel(content)) {
                contentContainer.addContent(content);
            }
        }

        metadata = metadataExtractor.extractAtFirstLevel(data);

        return new ContentContainerExt(metadata, contentContainer);
    }
}
