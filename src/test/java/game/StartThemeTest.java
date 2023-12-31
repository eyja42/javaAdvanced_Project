package game;

import org.junit.Test;

import static org.junit.Assert.*;

public class StartThemeTest {

    @Test
    public void loadGameAvailable() throws Exception{
        System.out.println("---loadGameAvailableTest---");
        if(StartTheme.loadGameAvailable()){
            System.out.println("true");
        }else{
            System.out.println("false");
        }
        System.out.println("---loadGameAvailableTest passed---");
    }
}