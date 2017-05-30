package api.util.filter;

import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.ui.RS2Widget;

/**
 * Created by Krulvis on 11-Apr-17.
 */
public class TextFilter implements Filter<RS2Widget> {

    private final String text;

    public TextFilter(String text) {
        this.text = text;
    }

    @Override
    public boolean match(RS2Widget widget) {
        String txt = widget != null && widget.getMessage() != null ? widget.getMessage() : "";
        return txt.toLowerCase().contains(text.toLowerCase());
    }

}
