package org.example.maeum2_be.dto;

public class GPTResponseDTO {
    private String id;
    private String object;
    private long created;
    private String model;
    private String system_fingerprint;
    private ChoiceDTO choices;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSystem_fingerprint() {
        return system_fingerprint;
    }

    public void setSystem_fingerprint(String system_fingerprint) {
        this.system_fingerprint = system_fingerprint;
    }

    public ChoiceDTO getChoices() {
        return choices;
    }

    public void setChoices(ChoiceDTO choices) {
        this.choices = choices;
    }
}