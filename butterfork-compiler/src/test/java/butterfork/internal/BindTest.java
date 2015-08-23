package butterfork.internal;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.ASSERT;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class BindTest {
  @Test public void bindingView() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterfork.Bind;",
        "import butterfork.BindResources;",
        "@BindResources(butterfork.internal.R.class)",
        "public class Test extends Activity {",
        "    @Bind(\"one\") View thing;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterfork.ButterFork;",
            "import butterfork.internal.R;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterFork.ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, R.id.one, \"field 'thing'\");",
            "    target.thing = view;",
            "  }",
            "  @Override public void unbind(T target) {",
            "    target.thing = null;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterForkProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }
  @Test public void bindingViewTooManyIdsFail() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterfork.Bind;",
        "import butterfork.BindResources;",
        "@BindResources(butterfork.internal.R.class)",
        "public class Test extends Activity {",
        "    @Bind({\"one\", \"two\"}) View thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterForkProcessor())
        .failsToCompile()
        .withErrorContaining("@Bind for a view must only specify one ID. Found: [one, two]. (test.Test.thing)")
        .in(source).onLine(8);
  }

  @Test public void bindingInterface() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterfork.Bind;",
        "import butterfork.BindResources;",
        "@BindResources(butterfork.internal.R.class)",
        "public class Test extends Activity {",
        "    interface TestInterface {}",
        "    @Bind(\"one\") TestInterface thing;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterfork.ButterFork;",
            "import butterfork.internal.R;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterFork.ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, R.id.one, \"field 'thing'\");",
            "    target.thing = finder.castView(view, R.id.one, \"field 'thing'\");",
            "  }",
            "  @Override public void unbind(T target) {",
            "    target.thing = null;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterForkProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void genericType() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.widget.EditText;",
        "import android.widget.TextView;",
        "import butterfork.Bind;",
        "import butterfork.BindResources;",
        "@BindResources(butterfork.internal.R.class)",
        "class Test<T extends TextView> extends Activity {",
        "    @Bind(\"one\") T thing;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterfork.ButterFork;",
            "import butterfork.internal.R;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterFork.ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, R.id.one, \"field 'thing'\");",
            "    target.thing = finder.castView(view, R.id.one, \"field 'thing'\");",
            "  }",
            "  @Override public void unbind(T target) {",
            "    target.thing = null;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterForkProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void oneFindPerId() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterfork.Bind;",
        "import butterfork.BindResources;",
        "import butterfork.OnClick;",
        "@BindResources(butterfork.internal.R.class)",
        "public class Test extends Activity {",
        "  @Bind(\"one\") View thing1;",
        "  @OnClick(\"one\") void doStuff() {}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterfork.ButterFork;",
            "import butterfork.internal.DebouncingOnClickListener;",
            "import butterfork.internal.R;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterFork.ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, R.id.one, \"field 'thing1' and method 'doStuff'\");",
            "    target.thing1 = view;",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override public void doClick(View p0) {",
            "        target.doStuff();",
            "      }",
            "    });",
            "  }",
            "  @Override public void unbind(T target) {",
            "    target.thing1 = null;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterForkProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void fieldVisibility() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterfork.Bind;",
        "import butterfork.BindResources;",
        "@BindResources(butterfork.internal.R.class)",
        "public class Test extends Activity {",
        "  @Bind(\"one\") public View thing1;",
        "  @Bind(\"two\") View thing2;",
        "  @Bind(\"three\") protected View thing3;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterForkProcessor())
        .compilesWithoutError();
  }

  @Test public void nullable() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterfork.Bind;",
        "import butterfork.BindResources;",
        "@BindResources(butterfork.internal.R.class)",
        "public class Test extends Activity {",
        "  @interface Nullable {}",
        "  @Nullable @Bind(\"one\") View view;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterfork.ButterFork;",
            "import butterfork.internal.R;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterFork.ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findOptionalView(source, R.id.one, null);",
            "    target.view = view;",
            "  }",
            "  @Override public void unbind(T target) {",
            "    target.view = null;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterForkProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void superclass() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterfork.Bind;",
        "import butterfork.BindResources;",
        "@BindResources(butterfork.internal.R.class)",
        "public class Test extends Activity {",
        "  @Bind(\"one\") View view;",
        "}",
        "class TestOne extends Test {",
        "  @Bind(\"one\") View thing;",
        "}",
        "class TestTwo extends Test {",
        "}"
    ));

    JavaFileObject expectedSource1 = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterfork.ButterFork;",
            "import butterfork.internal.R;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterFork.ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, R.id.one, \"field 'view'\");",
            "    target.view = view;",
            "  }",
            "  @Override public void unbind(T target) {",
            "    target.view = null;",
            "  }",
            "}"
        ));

    JavaFileObject expectedSource2 = JavaFileObjects.forSourceString("test/TestOne$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterfork.ButterFork;",
            "import butterfork.internal.R;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class TestOne$$ViewBinder<T extends TestOne> ",
            "    extends Test$$ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    super.bind(finder, target, source);",
            "    View view;",
            "    view = finder.findRequiredView(source, R.id.one, \"field 'thing'\");",
            "    target.thing = view;",
            "  }",
            "  @Override public void unbind(T target) {",
            "    super.unbind(target);",
            "    target.thing = null;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterForkProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource1, expectedSource2);
  }

  @Test public void genericSuperclass() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterfork.Bind;",
        "import butterfork.BindResources;",
        "@BindResources(butterfork.internal.R.class)",
        "public class Test<T> extends Activity {",
        "  @Bind(\"one\") View view;",
        "}",
        "class TestOne extends Test<String> {",
        "  @Bind(\"one\") View thing;",
        "}",
        "class TestTwo extends Test<Object> {",
        "}"
    ));

    JavaFileObject expectedSource1 = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterfork.ButterFork;",
            "import butterfork.internal.R;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterFork.ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, R.id.one, \"field 'view'\");",
            "    target.view = view;",
            "  }",
            "  @Override public void unbind(T target) {",
            "    target.view = null;",
            "  }",
            "}"
        ));

    JavaFileObject expectedSource2 = JavaFileObjects.forSourceString("test/TestOne$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterfork.ButterFork;",
            "import butterfork.internal.R;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class TestOne$$ViewBinder<T extends TestOne> ",
            "    extends Test$$ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    super.bind(finder, target, source);",
            "    View view;",
            "    view = finder.findRequiredView(source, R.id.one, \"field 'thing'\");",
            "    target.thing = view;",
            "  }",
            "  @Override public void unbind(T target) {",
            "    super.unbind(target);",
            "    target.thing = null;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterForkProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource1, expectedSource2);
  }

  @Test public void failsInJavaPackage() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package java.test;",
        "import android.view.View;",
        "import butterfork.Bind;",
        "public class Test {",
        "  @Bind(\"one\") View thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterForkProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@Bind-annotated class incorrectly in Java framework package. (java.test.Test)")
        .in(source).onLine(5);
  }

  @Test public void failsInAndroidPackage() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package android.test;",
        "import android.view.View;",
        "import butterfork.Bind;",
        "public class Test {",
        "  @Bind(\"one\") View thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterForkProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@Bind-annotated class incorrectly in Android framework package. (android.test.Test)")
        .in(source).onLine(5);
  }

  @Test public void failsIfInPrivateClass() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.view.View;",
        "import butterfork.Bind;",
        "public class Test {",
        "  private static class Inner {",
        "    @Bind(\"one\") View thing;",
        "  }",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterForkProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@Bind fields may not be contained in private classes. (test.Test.Inner.thing)")
        .in(source).onLine(5);
  }

  @Test public void failsIfNotView() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterfork.Bind;",
        "import butterfork.BindResources;",
        "@BindResources(butterfork.internal.R.class)",
        "public class Test extends Activity {",
        "  @Bind(\"one\") String thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterForkProcessor())
        .failsToCompile()
        .withErrorContaining("@Bind fields must extend from View or be an interface. (test.Test.thing)")
        .in(source).onLine(7);
  }

  @Test public void failsIfInInterface() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.view.View;",
        "import butterfork.Bind;",
        "public interface Test {",
        "    @Bind(\"one\") View thing = null;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterForkProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@Bind fields may only be contained in classes. (test.Test.thing)")
        .in(source).onLine(4);
  }

  @Test public void failsIfPrivate() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterfork.Bind;",
        "import butterfork.BindResources;",
        "@BindResources(butterfork.internal.R.class)",
        "public class Test extends Activity {",
        "    @Bind(\"one\") private View thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterForkProcessor())
        .failsToCompile()
        .withErrorContaining("@Bind fields must not be private or static. (test.Test.thing)")
        .in(source).onLine(8);
  }

  @Test public void failsIfStatic() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterfork.Bind;",
        "import butterfork.BindResources;",
        "@BindResources(butterfork.internal.R.class)",
        "public class Test extends Activity {",
        "    @Bind(\"one\") static View thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterForkProcessor())
        .failsToCompile()
        .withErrorContaining("@Bind fields must not be private or static. (test.Test.thing)")
        .in(source).onLine(8);
  }

  @Test public void duplicateBindingFails() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterfork.Bind;",
        "import butterfork.BindResources;",
        "@BindResources(butterfork.internal.R.class)",
        "public class Test extends Activity {",
        "    @Bind(\"one\") View thing1;",
        "    @Bind(\"one\") View thing2;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterForkProcessor())
        .failsToCompile()
        .withErrorContaining(
            "Attempt to use @Bind for an already bound ID one on 'thing1'. (test.Test.thing2)")
        .in(source).onLine(9);
  }

  @Test public void failsRootViewBindingWithBadTarget() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
        Joiner.on('\n').join(
            "package test;",
            "import android.content.Context;",
            "import android.view.View;",
            "import butterfork.OnItemClick;",
            "public class Test extends View {",
            "  @OnItemClick void doStuff() {}",
            "  public Test(Context context) {",
            "    super(context);",
            "  }",
            "}"));

    ASSERT.about(javaSource())
        .that(source)
        .processedWith(new ButterForkProcessor())
        .failsToCompile()
        .withErrorContaining((
            "@OnItemClick annotation without an ID may only be used with an object of type "
                + "\"android.widget.AdapterView<?>\" or an interface. (test.Test.doStuff)"))
        .in(source)
        .onLine(6);
  }

  @Test public void failsOptionalRootViewBinding() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
        Joiner.on('\n').join(
            "package test;",
            "import android.content.Context;",
            "import android.view.View;",
            "import butterfork.OnClick;",
            "public class Test extends View {",
            "  @interface Nullable {}",
            "  @Nullable @OnClick void doStuff() {}",
            "  public Test(Context context) {",
            "    super(context);",
            "  }",
            "}"));

    ASSERT.about(javaSource())
        .that(source)
        .processedWith(new ButterForkProcessor())
        .failsToCompile()
        .withErrorContaining(
            ("ID-free binding must not be annotated with @Nullable. (test.Test.doStuff)"))
        .in(source)
        .onLine(7);
  }

  @Test public void bindingArray() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterfork.Bind;",
        "import butterfork.BindResources;",
        "@BindResources(butterfork.internal.R.class)",
        "public class Test extends Activity {",
        "    @Bind({\"one\", \"two\", \"three\"}) View[] thing;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinding",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterfork.ButterFork;",
            "import butterfork.internal.R;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterFork.ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    View view;",
            "    target.thing = ButterFork.Finder.arrayOf(",
            "        finder.<View>findRequiredView(source, R.id.one, \"field 'thing'\"),",
            "        finder.<View>findRequiredView(source, R.id.two, \"field 'thing'\"),",
            "        finder.<View>findRequiredView(source, R.id.three, \"field 'thing'\")",
            "    );",
            "  }",
            "  @Override public void unbind(T target) {",
            "    target.thing = null;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterForkProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void bindingArrayWithGenerics() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterfork.Bind;",
        "import butterfork.BindResources;",
        "@BindResources(butterfork.internal.R.class)",
        "public class Test<T extends View> extends Activity {",
        "    @Bind({\"one\", \"two\", \"three\"}) T[] thing;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterfork.ButterFork;",
            "import butterfork.internal.R;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterFork.ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    View view;",
            "    target.thing = ButterFork.Finder.arrayOf(",
            "        finder.<View>findRequiredView(source, R.id.one, \"field 'thing'\"),",
            "        finder.<View>findRequiredView(source, R.id.two, \"field 'thing'\"),",
            "        finder.<View>findRequiredView(source, R.id.three, \"field 'thing'\")",
            "    );",
            "  }",
            "  @Override public void unbind(T target) {",
            "    target.thing = null;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterForkProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void bindingArrayWithCast() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.widget.TextView;",
        "import butterfork.Bind;",
        "import butterfork.BindResources;",
        "@BindResources(butterfork.internal.R.class)",
        "public class Test extends Activity {",
        "    @Bind({\"one\", \"two\", \"three\"}) TextView[] thing;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import android.widget.TextView;",
            "import butterfork.ButterFork;",
            "import butterfork.internal.R;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterFork.ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    View view;",
            "    target.thing = ButterFork.Finder.arrayOf(",
            "        finder.<TextView>findRequiredView(source, R.id.one, \"field 'thing'\"),",
            "        finder.<TextView>findRequiredView(source, R.id.two, \"field 'thing'\"),",
            "        finder.<TextView>findRequiredView(source, R.id.three, \"field 'thing'\")",
            "    );",
            "  }",
            "  @Override public void unbind(T target) {",
            "    target.thing = null;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterForkProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void bindingList() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterfork.Bind;",
        "import butterfork.BindResources;",
        "import java.util.List;",
        "@BindResources(butterfork.internal.R.class)",
        "public class Test extends Activity {",
        "    @Bind({\"one\", \"two\", \"three\"}) List<View> thing;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterfork.ButterFork;",
            "import butterfork.internal.R;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterFork.ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    View view;",
            "    target.thing = ButterFork.Finder.listOf(",
            "        finder.<View>findRequiredView(source, R.id.one, \"field 'thing'\"),",
            "        finder.<View>findRequiredView(source, R.id.two, \"field 'thing'\"),",
            "        finder.<View>findRequiredView(source, R.id.three, \"field 'thing'\")",
            "    );",
            "  }",
            "  @Override public void unbind(T target) {",
            "    target.thing = null;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterForkProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void bindingListOfInterface() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterfork.Bind;",
        "import butterfork.BindResources;",
        "import java.util.List;",
        "@BindResources(butterfork.internal.R.class)",
        "public class Test {",
        "    interface TestInterface {}",
        "    @Bind({\"one\", \"two\", \"three\"}) List<TestInterface> thing;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterfork.ButterFork;",
            "import butterfork.internal.R;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterFork.ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    View view;",
            "    target.thing = ButterFork.Finder.listOf(",
            "        finder.<Test.TestInterface>findRequiredView(source, R.id.one, \"field 'thing'\"),",
            "        finder.<Test.TestInterface>findRequiredView(source, R.id.two, \"field 'thing'\"),",
            "        finder.<Test.TestInterface>findRequiredView(source, R.id.three, \"field 'thing'\")",
            "    );",
            "  }",
            "  @Override public void unbind(T target) {",
            "    target.thing = null;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterForkProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void bindingListWithGenerics() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterfork.Bind;",
        "import butterfork.BindResources;",
        "import java.util.List;",
        "@BindResources(butterfork.internal.R.class)",
        "public class Test<T extends View> extends Activity {",
        "    @Bind({\"one\", \"two\", \"three\"}) List<T> thing;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterfork.ButterFork;",
            "import butterfork.internal.R;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterFork.ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    View view;",
            "    target.thing = ButterFork.Finder.listOf(",
            "        finder.<View>findRequiredView(source, R.id.one, \"field 'thing'\"),",
            "        finder.<View>findRequiredView(source, R.id.two, \"field 'thing'\"),",
            "        finder.<View>findRequiredView(source, R.id.three, \"field 'thing'\")",
            "    );",
            "  }",
            "  @Override public void unbind(T target) {",
            "    target.thing = null;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterForkProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void nullableList() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterfork.Bind;",
        "import butterfork.BindResources;",
        "import java.util.List;",
        "@BindResources(butterfork.internal.R.class)",
        "public class Test extends Activity {",
        "    @interface Nullable {}",
        "    @Nullable @Bind({\"one\", \"two\", \"three\"}) List<View> thing;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterfork.ButterFork;",
            "import butterfork.internal.R;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterFork.ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    View view;",
            "    target.thing = ButterFork.Finder.listOf(",
            "        finder.<View>findOptionalView(source, R.id.one, \"field 'thing'\"),",
            "        finder.<View>findOptionalView(source, R.id.two, \"field 'thing'\"),",
            "        finder.<View>findOptionalView(source, R.id.three, \"field 'thing'\")",
            "    );",
            "  }",
            "  @Override public void unbind(T target) {",
            "    target.thing = null;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterForkProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void failsIfNoIds() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.view.View;",
        "import butterfork.Bind;",
        "import java.util.List;",
        "public class Test {",
        "  @Bind({}) List<View> thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterForkProcessor())
        .failsToCompile()
        .withErrorContaining("@Bind must specify at least one ID. (test.Test.thing)")
        .in(source).onLine(6);
  }

  @Test public void failsIfNoGenericType() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import butterfork.Bind;",
        "import java.util.List;",
        "public class Test {",
        "  @Bind(\"one\") List thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterForkProcessor())
        .failsToCompile()
        .withErrorContaining("@Bind List must have a generic component. (test.Test.thing)")
        .in(source).onLine(5);
  }

  @Test public void failsIfUnsupportedCollection() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.view.View;",
        "import butterfork.Bind;",
        "import java.util.Deque;",
        "public class Test {",
        "  @Bind(\"one\") Deque<View> thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterForkProcessor())
        .failsToCompile()
        .withErrorContaining("@Bind must be a List or array. (test.Test.thing)")
        .in(source).onLine(6);
  }

  @Test public void failsIfGenericNotView() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterfork.Bind;",
        "import butterfork.BindResources;",
        "import java.util.List;",
        "@BindResources(butterfork.internal.R.class)",
        "public class Test extends Activity {",
        "  @Bind(\"one\") List<String> thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterForkProcessor())
        .failsToCompile()
        .withErrorContaining("@Bind List or array type must extend from View or be an interface. (test.Test.thing)")
        .in(source).onLine(8);
  }

  @Test public void failsIfArrayNotView() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterfork.Bind;",
        "import butterfork.BindResources;",
        "@BindResources(butterfork.internal.R.class)",
        "public class Test extends Activity {",
        "  @Bind(\"one\") String[] thing;",
        "}"));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterForkProcessor())
        .failsToCompile()
        .withErrorContaining("@Bind List or array type must extend from View or be an interface. (test.Test.thing)")
        .in(source).onLine(7);
  }

  @Test public void failsIfContainsDuplicateIds() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterfork.Bind;",
        "import butterfork.BindResources;",
        "import java.util.List;",
        "@BindResources(butterfork.internal.R.class)",
        "public class Test extends Activity {",
        "    @Bind({\"one\", \"one\"}) List<View> thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterForkProcessor())
        .failsToCompile()
        .withErrorContaining("@Bind annotation contains duplicate ID one. (test.Test.thing)")
        .in(source).onLine(9);
  }
}
