    // Webapp URLs    
    file:////home/roger/development/romartin/lienzo-webapp/target/lienzo-webapp-7.48.0-SNAPSHOT/LienzoShowcase.html
    http://localhost/lienzo-webapp-7.48.0-SNAPSHOT/LienzoShowcase.html

    // Examples webapp usage
    window.jsLienzoExamples.goToExample(0);

    // JsLienzo - Move shape
    window.jsLienzo.move(window.jsLienzo.getShape('rectangle'), 300, 300);

    // JsLienzo - Click & Move shape
    var r = window.jsLienzo.getShape('r');
    window.jsLienzo.click(r);
    window.jsLienzo.move(r, 100, 100);

    // Create shapes
    new com.ait.lienzo.client.core.shape.Rectangle(100, 100)
    var s = new com.ait.lienzo.client.core.shape.Text("Hello World", null, null, -1)
