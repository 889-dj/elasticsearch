/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.core.ml.inference.trainedmodel;

import org.elasticsearch.Version;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.xcontent.ConstructingObjectParser;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.core.Nullable;
import org.elasticsearch.xpack.core.ml.utils.ExceptionsHelper;

import java.io.IOException;
import java.util.Objects;

public class BertPassThroughConfig implements NlpConfig {

    public static final String NAME = "bert_pass_through";

    public static BertPassThroughConfig fromXContentStrict(XContentParser parser) {
        return STRICT_PARSER.apply(parser, null);
    }

    public static BertPassThroughConfig fromXContentLenient(XContentParser parser) {
        return LENIENT_PARSER.apply(parser, null);
    }

    private static final ConstructingObjectParser<BertPassThroughConfig, Void> STRICT_PARSER = createParser(false);
    private static final ConstructingObjectParser<BertPassThroughConfig, Void> LENIENT_PARSER = createParser(true);

    private static ConstructingObjectParser<BertPassThroughConfig, Void> createParser(boolean ignoreUnknownFields) {
        ConstructingObjectParser<BertPassThroughConfig, Void> parser = new ConstructingObjectParser<>(NAME, ignoreUnknownFields,
            a -> new BertPassThroughConfig((VocabularyConfig) a[0], (TokenizationParams) a[1]));
        parser.declareObject(ConstructingObjectParser.constructorArg(), VocabularyConfig.createParser(ignoreUnknownFields), VOCABULARY);
        parser.declareObject(ConstructingObjectParser.optionalConstructorArg(), TokenizationParams.createParser(ignoreUnknownFields),
            TOKENIZATION_PARAMS);
        return parser;
    }

    private final VocabularyConfig vocabularyConfig;
    private final TokenizationParams tokenizationParams;

    public BertPassThroughConfig(VocabularyConfig vocabularyConfig, @Nullable TokenizationParams tokenizationParams) {
        this.vocabularyConfig = ExceptionsHelper.requireNonNull(vocabularyConfig, VOCABULARY);
        this.tokenizationParams = tokenizationParams == null ? TokenizationParams.createDefault() : tokenizationParams;
    }

    public BertPassThroughConfig(StreamInput in) throws IOException {
        vocabularyConfig = new VocabularyConfig(in);
        tokenizationParams = new TokenizationParams(in);
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        builder.field(VOCABULARY.getPreferredName(), vocabularyConfig);
        builder.field(TOKENIZATION_PARAMS.getPreferredName(), tokenizationParams);
        builder.endObject();
        return builder;
    }

    @Override
    public String getWriteableName() {
        return NAME;
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        vocabularyConfig.writeTo(out);
        tokenizationParams.writeTo(out);
    }

    @Override
    public boolean isTargetTypeSupported(TargetType targetType) {
        return false;
    }

    @Override
    public Version getMinimalSupportedVersion() {
        return Version.V_8_0_0;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BertPassThroughConfig that = (BertPassThroughConfig) o;
        return Objects.equals(vocabularyConfig, that.vocabularyConfig)
            && Objects.equals(tokenizationParams, that.tokenizationParams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vocabularyConfig, tokenizationParams);
    }

    @Override
    public VocabularyConfig getVocabularyConfig() {
        return vocabularyConfig;
    }

    @Override
    public TokenizationParams getTokenizationParams() {
        return tokenizationParams;
    }
}
