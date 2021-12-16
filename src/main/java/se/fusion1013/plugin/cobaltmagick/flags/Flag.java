package se.fusion1013.plugin.cobaltmagick.flags;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.regex.Pattern;

public class Flag<T> {

    private static final Pattern VALID_NAME = Pattern.compile("^[:A-Za-z0-9\\-]{1,40}$");
    private final String name;
    private T value;

    public Flag(String name){
        if (name != null && !isValidName(name))
            throw new IllegalArgumentException("Invalid flag name used");
        this.name = name;
    }

    public void setValue(T value){
        this.value = value;
    }

    public final String getName(){
        return name;
    }

    public T getDefault(){
        return null;
    }

    public static boolean isValidName(String name){
        checkNotNull(name, "name");
        return VALID_NAME.matcher(name).matches();
    }
}
