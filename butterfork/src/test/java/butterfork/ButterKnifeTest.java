package butterfork;

import android.app.Activity;
import android.util.Property;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;

import butterfork.shadow.EditModeShadowView;

import static butterfork.ButterKnife.Finder.arrayOf;
import static butterfork.ButterKnife.Finder.listOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.assertj.core.api.Assertions.fail;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ButterKnifeTest {
  private static final Property<View, Boolean> PROPERTY_ENABLED =
      new Property<View, Boolean>(Boolean.class, "enabled") {
        @Override public Boolean get(View view) {
          return view.isEnabled();
        }

        @Override public void set(View view, Boolean enabled) {
          view.setEnabled(enabled);
        }
      };
  private static final ButterKnife.Setter<View, Boolean> SETTER_ENABLED =
      new ButterKnife.Setter<View, Boolean>() {
        @Override public void set(View view, Boolean value, int index) {
          view.setEnabled(value);
        }
      };
  private static final ButterKnife.Action<View> ACTION_DISABLE = new ButterKnife.Action<View>() {
    @Override public void apply(View view, int index) {
      view.setEnabled(false);
    }
  };

  @Before @After // Clear out cache of binders before and after each test.
  public void resetViewsCache() {
    ButterKnife.BINDERS.clear();
  }

  @Test public void listOfFiltersNull() {
    assertThat(listOf(null, null, null)).isEmpty();
    assertThat(listOf("One", null, null)).containsExactly("One");
    assertThat(listOf(null, "One", null)).containsExactly("One");
    assertThat(listOf(null, null, "One")).containsExactly("One");
    assertThat(listOf("One", "Two", null)).containsExactly("One", "Two");
    assertThat(listOf("One", null, "Two")).containsExactly("One", "Two");
    assertThat(listOf(null, "One", "Two")).containsExactly("One", "Two");
  }

  @Test public void arrayOfFiltersNull() {
    assertThat(arrayOf(null, null, null)).isEmpty();
    assertThat(arrayOf("One", null, null)).containsExactly("One");
    assertThat(arrayOf(null, "One", null)).containsExactly("One");
    assertThat(arrayOf(null, null, "One")).containsExactly("One");
    assertThat(arrayOf("One", "Two", null)).containsExactly("One", "Two");
    assertThat(arrayOf("One", null, "Two")).containsExactly("One", "Two");
    assertThat(arrayOf(null, "One", "Two")).containsExactly("One", "Two");
  }

  @Test public void propertyAppliedToEveryView() {
    View view1 = new View(RuntimeEnvironment.application);
    View view2 = new View(RuntimeEnvironment.application);
    View view3 = new View(RuntimeEnvironment.application);
    assertThat(view1.isEnabled()).isTrue();
    assertThat(view2.isEnabled()).isTrue();
    assertThat(view3.isEnabled()).isTrue();

    List<View> views = Arrays.asList(view1, view2, view3);
    ButterKnife.apply(views, PROPERTY_ENABLED, false);

    assertThat(view1.isEnabled()).isFalse();
    assertThat(view2.isEnabled()).isFalse();
    assertThat(view3.isEnabled()).isFalse();
  }

  @Test public void actionAppliedToEveryView() {
    View view1 = new View(RuntimeEnvironment.application);
    View view2 = new View(RuntimeEnvironment.application);
    View view3 = new View(RuntimeEnvironment.application);
    assertThat(view1.isEnabled()).isTrue();
    assertThat(view2.isEnabled()).isTrue();
    assertThat(view3.isEnabled()).isTrue();

    List<View> views = Arrays.asList(view1, view2, view3);
    ButterKnife.apply(views, ACTION_DISABLE);

    assertThat(view1.isEnabled()).isFalse();
    assertThat(view2.isEnabled()).isFalse();
    assertThat(view3.isEnabled()).isFalse();
  }

  @Test public void setterAppliedToEveryView() {
    View view1 = new View(RuntimeEnvironment.application);
    View view2 = new View(RuntimeEnvironment.application);
    View view3 = new View(RuntimeEnvironment.application);
    assertThat(view1.isEnabled()).isTrue();
    assertThat(view2.isEnabled()).isTrue();
    assertThat(view3.isEnabled()).isTrue();

    List<View> views = Arrays.asList(view1, view2, view3);
    ButterKnife.apply(views, SETTER_ENABLED, false);

    assertThat(view1.isEnabled()).isFalse();
    assertThat(view2.isEnabled()).isFalse();
    assertThat(view3.isEnabled()).isFalse();
  }

  @Test public void zeroBindingsBindDoesNotThrowException() {
    class Example {
    }

    Example example = new Example();
    ButterKnife.bind(example, null, null);
    assertThat(ButterKnife.BINDERS).contains(entry(Example.class, ButterKnife.NOP_VIEW_BINDER));
  }

  @Test public void zeroBindingsUnbindDoesNotThrowException() {
    class Example {
    }

    Example example = new Example();
    ButterKnife.unbind(example);
    assertThat(ButterKnife.BINDERS).contains(entry(Example.class, ButterKnife.NOP_VIEW_BINDER));
  }

  @Test public void bindingKnownPackagesIsNoOp() {
    ButterKnife.bind(Robolectric.buildActivity(Activity.class).create().get());
    assertThat(ButterKnife.BINDERS).isEmpty();
    ButterKnife.bind(new Object(), Robolectric.buildActivity(Activity.class).create().get());
    assertThat(ButterKnife.BINDERS).isEmpty();
  }

  @Test public void finderThrowsNiceError() {
    View view = new View(RuntimeEnvironment.application);
    try {
      ButterKnife.Finder.VIEW.findRequiredView(view, android.R.id.button1, "yo mama");
      fail("View 'button1' with ID " + android.R.id.button1 + " should not have been found.");
    } catch (IllegalStateException e) {
      assertThat(e).hasMessage("Required view 'button1' with ID "
          + android.R.id.button1
          + " for yo mama was not found. If this view is optional add '@Nullable' annotation.");
    }
  }

  @Config(shadows = EditModeShadowView.class)
  @Test public void finderThrowsLessNiceErrorInEditMode() {
    View view = new View(RuntimeEnvironment.application);
    try {
      ButterKnife.Finder.VIEW.findRequiredView(view, android.R.id.button1, "yo mama");
      fail("View 'button1' with ID " + android.R.id.button1 + " should not have been found.");
    } catch (IllegalStateException e) {
      assertThat(e).hasMessage("Required view '<unavailable while editing>' "
          + "with ID " + android.R.id.button1
          + " for yo mama was not found. If this view is optional add '@Nullable' annotation.");
    }
  }
}
