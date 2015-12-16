package co.ryred.checkerservlet.Spigot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Beth on 16/12/2015.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ID {

    private String originalString;
    private int id;
    private boolean error;

}
