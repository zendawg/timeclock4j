package org.statefive.timeclockj.android.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import org.statefive.timeclockj.android.ClockActivity;

public class ClockActivityTest extends ActivityInstrumentationTestCase2<ClockActivity> {

  public ClockActivityTest() {
    super("org.statefive.timeclockj.android", ClockActivity.class);
  }

  @Override
  public void setUp() {
  }

  public void testActivity() {
    ClockActivity activity = getActivity();
//    Spinner spinnerDicts = (Spinner) activity.findViewById(org.statefive.timeclockj.android.R.id.spinnerChooseDict);
//    assertEquals(0, spinnerDicts.getCount());
//    assertNotNull(activity);
  }

  public void testInsertNewDictionaryActivity() {
    ClockActivity activity = getActivity();
    assertNotNull(activity);

    final Button buttonClock = (Button) activity.findViewById(org.statefive.timeclockj.android.R.id.buttonClock);
//    buttonCreateNewDict.performClick();
//    assertEquals(1, dbHelper.getDictionaryRowCount(db));
//
//    Spinner spinnerDicts = (Spinner) activity.findViewById(org.statefive.ldict.android.R.id.spinnerChooseDict);
//    assertEquals(1, spinnerDicts.getCount());
  }
}
