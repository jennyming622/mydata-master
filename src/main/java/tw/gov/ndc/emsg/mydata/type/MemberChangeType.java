package tw.gov.ndc.emsg.mydata.type;

import org.apache.commons.lang3.StringUtils;

public enum MemberChangeType {
    Name("姓名"),
    Mobile("手機"),
    Email("電子信箱"),
    ;

    private String text;
    MemberChangeType(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static MemberChangeType concertToType(String text) {
        for (MemberChangeType type : MemberChangeType.values()) {
            if(StringUtils.equals(type.getText(), text)) {
                return type;
            }
        }
        return null;
    }

    public static MemberChangeType nameToType(String name) {
        for (MemberChangeType type : MemberChangeType.values()) {
            if(StringUtils.equals(type.name(), name)) {
                return type;
            }
        }
        return null;
    }
}
