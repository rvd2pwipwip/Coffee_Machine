package com.stingray.qello.firetv.android.contentbrowser;

import com.stingray.qello.firetv.android.contentbrowser.metadata.MetadataExtractor;
import com.stingray.qello.firetv.android.model.AModelTranslator;
import com.stingray.qello.firetv.android.model.SvodMetadata;
import com.stingray.qello.firetv.android.model.content.Content;
import com.stingray.qello.firetv.android.model.content.ContentContainer;
import com.stingray.qello.firetv.android.model.content.ContentContainerExt;
import com.stingray.qello.firetv.android.recipe.NoOpRecipeCallbacks;
import com.stingray.qello.firetv.android.recipe.Recipe;
import com.stingray.qello.firetv.android.utils.PathHelper;
import com.stingray.qello.firetv.dynamicparser.DynamicParser;
import com.stingray.qello.firetv.dynamicparser.IParser;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;
import java.util.Map;

public class ContentContainerExtFactory {
    private final MetadataExtractor metadataExtractor = new MetadataExtractor();

    public <T extends Content> ContentContainerExt create(String containerName, String data, DynamicParser dynamicParser, Recipe recipe, AModelTranslator<T> contentTranslator)
            throws JsonProcessingException, DynamicParser.ParserNotFoundException, IParser.InvalidQueryException,
            IParser.InvalidDataException, PathHelper.MalformedInjectionStringException {

        ContentContainer contentContainer = ContentContainer.newInstance(containerName);
        SvodMetadata metadata;

        List<Map<String, Object>> cookedJson = dynamicParser.parseInput(recipe, data, null);

        for (Map<String, Object> objectMap : cookedJson) {
            T content = (T) dynamicParser.translateMapToModel(recipe, new NoOpRecipeCallbacks(), objectMap);

            if (contentTranslator.validateModel(content)) {
                contentContainer.addContent(content);
            }
        }

        metadata = metadataExtractor.extractAtFirstLevel(data);

        return new ContentContainerExt(metadata, contentContainer);
    }
}
