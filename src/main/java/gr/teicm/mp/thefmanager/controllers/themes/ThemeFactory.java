package gr.teicm.mp.thefmanager.controllers.themes;

import gr.teicm.mp.thefmanager.models.themes.*;

/**
 * Created by Elias Myronidis on 24/3/2014.
 */
public class ThemeFactory {
    public boolean getTheme(String themeName){
        boolean themeIsSet = false;

        if(themeName == "napkin"){
            ITheme newTheme = new NapkinTheme();
            themeIsSet = newTheme.setTheme();
        }
        else if(themeName == "seaglass"){
            ITheme newTheme = new SeaglassTheme();
            themeIsSet = newTheme.setTheme();
        }
        else if(themeName == "quaqua"){
            ITheme newTheme = new QuaquaTheme();
            themeIsSet = newTheme.setTheme();
        }
        else if(themeName == "jtatAluminium"){
            ITheme newTheme = new AluminiumTheme();
            themeIsSet = newTheme.setTheme();
        }
        else if(themeName == "jtatHifi"){
            ITheme newTheme = new HiFITheme();
            themeIsSet = newTheme.setTheme();
        }
        else if(themeName == "jtatBernstein"){
            ITheme newTheme = new BernsteinTheme();
            themeIsSet = newTheme.setTheme();
        }
        return themeIsSet;

    }
}