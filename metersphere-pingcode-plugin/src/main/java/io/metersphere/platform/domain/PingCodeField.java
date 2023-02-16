package io.metersphere.platform.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PingCodeField {

    private String id;
    private String name;
    private String type;
    private List<Option> options;
    private boolean is_removable;
    private boolean is_name_editable;
    private boolean is_options_editable;

    @Setter
    @Getter
    public static class Option {
        private String _id;
        private String text;
    }

    @Setter
    @Getter
    public static class Priority {
        private String id;
        private String url;
        private String name;
    }

    @Setter
    @Getter
    public static class Sprint {
        private String id;
        private String url;
        private String name;
        private String start_at;
        private String end_at;
        private String status;
    }
}
