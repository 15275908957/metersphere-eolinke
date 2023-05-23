package entity;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.ParseException;

public class HeaderEntity implements Header {
    private String name;
    private String value;

    public HeaderEntity (String name, String value){
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public HeaderElement[] getElements() throws ParseException {
        return new HeaderElement[0];
    }
}