package com.suhendro.cookingtime;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by Suhendro on 9/18/2017.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        MainActivityScreenTest.class,
        DetailActivityScreenTest.class,
        CookingInstructionScreenTest.class
})
public class InstrumentsTestSuites {
}
