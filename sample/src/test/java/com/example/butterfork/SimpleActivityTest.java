package com.example.butterfork;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import butterfork.ButterFork;

import static org.assertj.android.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class) //
@Config(manifest = "src/main/AndroidManifest.xml")
public class SimpleActivityTest {
  @Test public void verifyContentViewBinding() {
    SimpleActivity activity = Robolectric.buildActivity(SimpleActivity.class) //
        .create() //
        .get();

    assertThat(activity.title).hasId(R.id.title);
    assertThat(activity.subtitle).hasId(R.id.subtitle);
    assertThat(activity.hello).hasId(R.id.hello);
    assertThat(activity.listOfThings).hasId(R.id.list_of_things);
    assertThat(activity.footer).hasId(R.id.footer);

    ButterFork.unbind(activity);
    assertThat(activity.title).isNull();
    assertThat(activity.subtitle).isNull();
    assertThat(activity.hello).isNull();
    assertThat(activity.listOfThings).isNull();
    assertThat(activity.footer).isNull();
  }
}
