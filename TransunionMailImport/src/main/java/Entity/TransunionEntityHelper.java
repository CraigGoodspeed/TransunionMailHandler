package Entity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TransunionEntityHelper{

    public static Map<String, Field> myFields = Arrays.stream(TransunionEntity.class.getDeclaredFields()).filter(
            item -> !item.isAnnotationPresent(Exclude.class)
    ).collect(Collectors.toMap((field) -> field.getName().toLowerCase(), (field) -> field));

    public static Map<String, Method> getMethods;

    static {
        getMethods = new HashMap();
        myFields.forEach( (key,field) ->
        {
            String tmp =String.format("get%s",key);
            Method[] getMethodsToMap = TransunionEntity.class.getMethods();
            if(!field.isAnnotationPresent(Exclude.class)) {
                try {
                    getMethods.put(key, Arrays.stream(getMethodsToMap).filter(item -> item.getName().toLowerCase().endsWith(tmp)).findFirst().get() );
                }
                catch(Exception e){
                    System.out.println(e.getMessage());
                }
            }
        });
    }

}