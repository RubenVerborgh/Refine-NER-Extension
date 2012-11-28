var Logger = Packages.org.slf4j.LoggerFactory.getLogger("NER-extension");

/* Initialize the extension. */
function init() {
  var ClientSideResourceManager = Packages.com.google.refine.ClientSideResourceManager;
  Logger.info("Initializing client resources");

  ClientSideResourceManager.addPaths(
    "project/scripts",
    module, [
      "scripts/menus.js",
    ]
  );

  ClientSideResourceManager.addPaths(
    "project/styles",
    module, [
      "styles/main.less",
    ]
  );
}
