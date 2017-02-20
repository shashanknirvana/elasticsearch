/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.xpack.ml.job.config;

import org.elasticsearch.common.ParseField;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.io.stream.Writeable;
import org.elasticsearch.common.xcontent.ConstructingObjectParser;
import org.elasticsearch.common.xcontent.ObjectParser;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class JobUpdate implements Writeable, ToXContent {
    public static final ParseField DETECTORS = new ParseField("detectors");

    public static final ObjectParser<Builder, Void> PARSER = new ObjectParser<>("job_udpate", Builder::new);

    static {
        PARSER.declareStringOrNull(Builder::setDescription, Job.DESCRIPTION);
        PARSER.declareObjectArray(Builder::setDetectorUpdates, DetectorUpdate.PARSER, DETECTORS);
        PARSER.declareObject(Builder::setModelDebugConfig, ModelDebugConfig.PARSER, Job.MODEL_DEBUG_CONFIG);
        PARSER.declareObject(Builder::setAnalysisLimits, AnalysisLimits.PARSER, Job.ANALYSIS_LIMITS);
        PARSER.declareLong(Builder::setBackgroundPersistInterval, Job.BACKGROUND_PERSIST_INTERVAL);
        PARSER.declareLong(Builder::setRenormalizationWindowDays, Job.RENORMALIZATION_WINDOW_DAYS);
        PARSER.declareLong(Builder::setResultsRetentionDays, Job.RESULTS_RETENTION_DAYS);
        PARSER.declareLong(Builder::setModelSnapshotRetentionDays, Job.MODEL_SNAPSHOT_RETENTION_DAYS);
        PARSER.declareStringArray(Builder::setCategorizationFilters, AnalysisConfig.CATEGORIZATION_FILTERS);
        PARSER.declareField(Builder::setCustomSettings, (p, c) -> p.map(), Job.CUSTOM_SETTINGS,  ObjectParser.ValueType.OBJECT);
        PARSER.declareString(Builder::setModelSnapshotId, Job.MODEL_SNAPSHOT_ID);
    }

    private final String description;
    private final List<DetectorUpdate> detectorUpdates;
    private final ModelDebugConfig modelDebugConfig;
    private final AnalysisLimits analysisLimits;
    private final Long renormalizationWindowDays;
    private final Long backgroundPersistInterval;
    private final Long modelSnapshotRetentionDays;
    private final Long resultsRetentionDays;
    private final List<String> categorizationFilters;
    private final Map<String, Object> customSettings;
    private final String modelSnapshotId;

    private JobUpdate(@Nullable String description, @Nullable List<DetectorUpdate> detectorUpdates,
                      @Nullable ModelDebugConfig modelDebugConfig, @Nullable AnalysisLimits analysisLimits,
                      @Nullable Long backgroundPersistInterval, @Nullable Long renormalizationWindowDays,
                      @Nullable Long resultsRetentionDays, @Nullable Long modelSnapshotRetentionDays,
                      @Nullable List<String> categorisationFilters, @Nullable  Map<String, Object> customSettings,
                      @Nullable String modelSnapshotId) {
        this.description = description;
        this.detectorUpdates = detectorUpdates;
        this.modelDebugConfig = modelDebugConfig;
        this.analysisLimits = analysisLimits;
        this.renormalizationWindowDays = renormalizationWindowDays;
        this.backgroundPersistInterval = backgroundPersistInterval;
        this.modelSnapshotRetentionDays = modelSnapshotRetentionDays;
        this.resultsRetentionDays = resultsRetentionDays;
        this.categorizationFilters = categorisationFilters;
        this.customSettings = customSettings;
        this.modelSnapshotId = modelSnapshotId;
    }

    public JobUpdate(StreamInput in) throws IOException {
        description = in.readOptionalString();
        if (in.readBoolean()) {
            detectorUpdates = in.readList(DetectorUpdate::new);
        } else {
            detectorUpdates = null;
        }
        modelDebugConfig = in.readOptionalWriteable(ModelDebugConfig::new);
        analysisLimits = in.readOptionalWriteable(AnalysisLimits::new);
        renormalizationWindowDays = in.readOptionalLong();
        backgroundPersistInterval = in.readOptionalLong();
        modelSnapshotRetentionDays = in.readOptionalLong();
        resultsRetentionDays = in.readOptionalLong();
        if (in.readBoolean()) {
            categorizationFilters = in.readList(StreamInput::readString);
        } else {
            categorizationFilters = null;
        }
        customSettings = in.readMap();
        modelSnapshotId = in.readOptionalString();
    }
    @Override
    public void writeTo(StreamOutput out) throws IOException {
        out.writeOptionalString(description);
        out.writeBoolean(detectorUpdates != null);
        if (detectorUpdates != null) {
            out.writeList(detectorUpdates);
        }
        out.writeOptionalWriteable(modelDebugConfig);
        out.writeOptionalWriteable(analysisLimits);
        out.writeOptionalLong(renormalizationWindowDays);
        out.writeOptionalLong(backgroundPersistInterval);
        out.writeOptionalLong(modelSnapshotRetentionDays);
        out.writeOptionalLong(resultsRetentionDays);
        out.writeBoolean(categorizationFilters != null);
        if (categorizationFilters != null) {
            out.writeStringList(categorizationFilters);
        }
        out.writeMap(customSettings);
        out.writeOptionalString(modelSnapshotId);
    }

    public String getDescription() {
        return description;
    }

    public List<DetectorUpdate> getDetectorUpdates() {
        return detectorUpdates;
    }

    public ModelDebugConfig getModelDebugConfig() {
        return modelDebugConfig;
    }

    public AnalysisLimits getAnalysisLimits() {
        return analysisLimits;
    }

    public Long getRenormalizationWindowDays() {
        return renormalizationWindowDays;
    }

    public Long getBackgroundPersistInterval() {
        return backgroundPersistInterval;
    }

    public Long getModelSnapshotRetentionDays() {
        return modelSnapshotRetentionDays;
    }

    public Long getResultsRetentionDays() {
        return resultsRetentionDays;
    }

    public List<String> getCategorizationFilters() {
        return categorizationFilters;
    }

    public Map<String, Object> getCustomSettings() {
        return customSettings;
    }

    public String getModelSnapshotId() {
        return modelSnapshotId;
    }

    public boolean isAutodetectProcessUpdate() {
        return modelDebugConfig != null || detectorUpdates != null;
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        if (description != null) {
            builder.field(Job.DESCRIPTION.getPreferredName(), description);
        }
        if (detectorUpdates != null) {
            builder.field(DETECTORS.getPreferredName(), detectorUpdates);
        }
        if (modelDebugConfig != null) {
            builder.field(Job.MODEL_DEBUG_CONFIG.getPreferredName(), modelDebugConfig);
        }
        if (analysisLimits != null) {
            builder.field(Job.ANALYSIS_LIMITS.getPreferredName(), analysisLimits);
        }
        if (renormalizationWindowDays != null) {
            builder.field(Job.RENORMALIZATION_WINDOW_DAYS.getPreferredName(), renormalizationWindowDays);
        }
        if (backgroundPersistInterval != null) {
            builder.field(Job.BACKGROUND_PERSIST_INTERVAL.getPreferredName(), backgroundPersistInterval);
        }
        if (modelSnapshotRetentionDays != null) {
            builder.field(Job.MODEL_SNAPSHOT_RETENTION_DAYS.getPreferredName(), modelSnapshotRetentionDays);
        }
        if (resultsRetentionDays != null) {
            builder.field(Job.RESULTS_RETENTION_DAYS.getPreferredName(), resultsRetentionDays);
        }
        if (categorizationFilters != null) {
            builder.field(AnalysisConfig.CATEGORIZATION_FILTERS.getPreferredName(), categorizationFilters);
        }
        if (customSettings != null) {
            builder.field(Job.CUSTOM_SETTINGS.getPreferredName(), customSettings);
        }
        if (modelSnapshotId != null) {
            builder.field(Job.MODEL_SNAPSHOT_ID.getPreferredName(), modelSnapshotId);
        }
        builder.endObject();
        return builder;
    }

    /**
     * Updates {@code source} with the new values in this object returning a new {@link Job}.
     *
     * @param source Source job to be updated
     * @return A new job equivalent to {@code source} updated.
     */
    public Job mergeWithJob(Job source) {
        Job.Builder builder = new Job.Builder(source);
        if (description != null) {
            builder.setDescription(description);
        }
        if (detectorUpdates != null && detectorUpdates.isEmpty() == false) {
            AnalysisConfig ac = source.getAnalysisConfig();
            int numDetectors = ac.getDetectors().size();
            for (DetectorUpdate dd : detectorUpdates) {
                if (dd.getIndex() >= numDetectors) {
                    throw new IllegalArgumentException("Detector index is >= the number of detectors");
                }

                Detector.Builder detectorbuilder = new Detector.Builder(ac.getDetectors().get(dd.getIndex()));
                if (dd.getDescription() != null) {
                    detectorbuilder.setDetectorDescription(dd.getDescription());
                }
                if (dd.getRules() != null) {
                    detectorbuilder.setDetectorRules(dd.getRules());
                }
                ac.getDetectors().set(dd.getIndex(), detectorbuilder.build());
            }

            AnalysisConfig.Builder acBuilder = new AnalysisConfig.Builder(ac);
            builder.setAnalysisConfig(acBuilder);
        }
        if (modelDebugConfig != null) {
            builder.setModelDebugConfig(modelDebugConfig);
        }
        if (analysisLimits != null) {
            builder.setAnalysisLimits(analysisLimits);
        }
        if (renormalizationWindowDays != null) {
            builder.setRenormalizationWindowDays(renormalizationWindowDays);
        }
        if (backgroundPersistInterval != null) {
            builder.setBackgroundPersistInterval(backgroundPersistInterval);
        }
        if (modelSnapshotRetentionDays != null) {
            builder.setModelSnapshotRetentionDays(modelSnapshotRetentionDays);
        }
        if (resultsRetentionDays != null) {
            builder.setResultsRetentionDays(resultsRetentionDays);
        }
        if (categorizationFilters != null) {
            AnalysisConfig.Builder analysisConfigBuilder = new AnalysisConfig.Builder(source.getAnalysisConfig());
            analysisConfigBuilder.setCategorizationFilters(categorizationFilters);
            builder.setAnalysisConfig(analysisConfigBuilder);
        }
        if (customSettings != null) {
            builder.setCustomSettings(customSettings);
        }
        if (modelSnapshotId != null) {
            builder.setModelSnapshotId(modelSnapshotId);
        }

        return builder.build();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other instanceof JobUpdate == false) {
            return false;
        }

        JobUpdate that = (JobUpdate) other;

        return Objects.equals(this.description, that.description)
                && Objects.equals(this.detectorUpdates, that.detectorUpdates)
                && Objects.equals(this.modelDebugConfig, that.modelDebugConfig)
                && Objects.equals(this.analysisLimits, that.analysisLimits)
                && Objects.equals(this.renormalizationWindowDays, that.renormalizationWindowDays)
                && Objects.equals(this.backgroundPersistInterval, that.backgroundPersistInterval)
                && Objects.equals(this.modelSnapshotRetentionDays, that.modelSnapshotRetentionDays)
                && Objects.equals(this.resultsRetentionDays, that.resultsRetentionDays)
                && Objects.equals(this.categorizationFilters, that.categorizationFilters)
                && Objects.equals(this.customSettings, that.customSettings)
                && Objects.equals(this.modelSnapshotId, that.modelSnapshotId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, detectorUpdates, modelDebugConfig, analysisLimits, renormalizationWindowDays,
                backgroundPersistInterval, modelSnapshotRetentionDays, resultsRetentionDays, categorizationFilters, customSettings,
                modelSnapshotId);
    }

    public static class DetectorUpdate implements Writeable, ToXContent {
        @SuppressWarnings("unchecked")
        public static final ConstructingObjectParser<DetectorUpdate, Void> PARSER =
                new ConstructingObjectParser<>("detector_update", a -> new DetectorUpdate((int) a[0], (String) a[1],
                        (List<DetectionRule>) a[2]));

        public static final ParseField INDEX = new ParseField("index");
        public static final ParseField RULES = new ParseField("rules");

        static {
            PARSER.declareInt(ConstructingObjectParser.optionalConstructorArg(), INDEX);
            PARSER.declareStringOrNull(ConstructingObjectParser.optionalConstructorArg(), Job.DESCRIPTION);
            PARSER.declareObjectArray(ConstructingObjectParser.optionalConstructorArg(), DetectionRule.PARSER, RULES);
        }

        private int index;
        private String description;
        private List<DetectionRule> rules;

        public DetectorUpdate(int index, String description, List<DetectionRule> rules) {
            this.index = index;
            this.description = description;
            this.rules = rules;
        }

        public DetectorUpdate(StreamInput in) throws IOException {
            index = in.readInt();
            description = in.readOptionalString();
            if (in.readBoolean()) {
                rules = in.readList(DetectionRule::new);
            } else {
                rules = null;
            }
        }

        public int getIndex() {
            return index;
        }

        public String getDescription() {
            return description;
        }

        public List<DetectionRule> getRules() {
            return rules;
        }

        @Override
        public void writeTo(StreamOutput out) throws IOException {
            out.writeInt(index);
            out.writeOptionalString(description);
            out.writeBoolean(rules != null);
            if (rules != null) {
                out.writeList(rules);
            }
        }

        @Override
        public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
            builder.startObject();

            builder.field(INDEX.getPreferredName(), index);
            if (description != null) {
                builder.field(Job.DESCRIPTION.getPreferredName(), description);
            }
            if (rules != null) {
                builder.field(RULES.getPreferredName(), rules);
            }
            builder.endObject();

            return builder;
        }

        @Override
        public int hashCode() {
            return Objects.hash(index, description, rules);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other instanceof DetectorUpdate == false) {
                return false;
            }

            DetectorUpdate that = (DetectorUpdate) other;
            return this.index == that.index && Objects.equals(this.description, that.description)
                    && Objects.equals(this.rules, that.rules);
        }
    }

    public static class Builder {
        private String description;
        private List<DetectorUpdate> detectorUpdates;
        private ModelDebugConfig modelDebugConfig;
        private AnalysisLimits analysisLimits;
        private Long renormalizationWindowDays;
        private Long backgroundPersistInterval;
        private Long modelSnapshotRetentionDays;
        private Long resultsRetentionDays;
        private List<String> categorizationFilters;
        private Map<String, Object> customSettings;
        private String modelSnapshotId;

        public Builder() {}

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setDetectorUpdates(List<DetectorUpdate> detectorUpdates) {
            this.detectorUpdates = detectorUpdates;
            return this;
        }

        public Builder setModelDebugConfig(ModelDebugConfig modelDebugConfig) {
            this.modelDebugConfig = modelDebugConfig;
            return this;
        }

        public Builder setAnalysisLimits(AnalysisLimits analysisLimits) {
            this.analysisLimits = analysisLimits;
            return this;
        }

        public Builder setRenormalizationWindowDays(Long renormalizationWindowDays) {
            this.renormalizationWindowDays = renormalizationWindowDays;
            return this;
        }

        public Builder setBackgroundPersistInterval(Long backgroundPersistInterval) {
            this.backgroundPersistInterval = backgroundPersistInterval;
            return this;
        }

        public Builder setModelSnapshotRetentionDays(Long modelSnapshotRetentionDays) {
            this.modelSnapshotRetentionDays = modelSnapshotRetentionDays;
            return this;
        }

        public Builder setResultsRetentionDays(Long resultsRetentionDays) {
            this.resultsRetentionDays = resultsRetentionDays;
            return this;
        }

        public Builder setCategorizationFilters(List<String> categorizationFilters) {
            this.categorizationFilters = categorizationFilters;
            return this;
        }

        public Builder setCustomSettings(Map<String, Object> customSettings) {
            this.customSettings = customSettings;
            return this;
        }

        public Builder setModelSnapshotId(String modelSnapshotId) {
            this.modelSnapshotId = modelSnapshotId;
            return this;
        }

        public JobUpdate build() {
            return new JobUpdate(description, detectorUpdates, modelDebugConfig, analysisLimits, backgroundPersistInterval,
                    renormalizationWindowDays, resultsRetentionDays, modelSnapshotRetentionDays, categorizationFilters, customSettings,
                    modelSnapshotId);
        }
    }
}
