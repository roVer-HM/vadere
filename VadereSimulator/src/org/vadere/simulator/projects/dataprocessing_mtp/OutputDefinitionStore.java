package org.vadere.simulator.projects.dataprocessing_mtp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OutputDefinitionStore {
    private List<OutputFileStore> files;
    private List<ProcessorStore> processors;
    private Map<String, AttributesProcessor> attributes;

    public OutputDefinitionStore() {
        this.files = new ArrayList<>();
        this.processors = new ArrayList<>();
    }

    public void addOutputFile(OutputFileStore file) {
        this.files.add(file);
    }

    public void addProcessor(ProcessorStore processor) {
        this.processors.add(processor);
    }

    public void addAttributes(AttributesProcessor attributes) {
        this.attributes.put(attributes.getClass().getName(), attributes);
    }
}
