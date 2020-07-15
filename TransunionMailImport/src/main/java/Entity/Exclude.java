package Entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
/**
 *The attribute is used so when we handle items from the csv file we can create temporary variables that will not be imported
 * Exclude ie - dont import me.
 */
public @interface Exclude {
}
