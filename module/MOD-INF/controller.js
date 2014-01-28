var logger = Packages.org.slf4j.LoggerFactory.getLogger("NER-extension"),
    File = Packages.java.io.File,
    refineServlet = Packages.com.google.refine.RefineServlet,
    operationRegistry = Packages.com.google.refine.operations.OperationRegistry,
    ner = Packages.org.freeyourmetadata.ner,
    services = ner.services,
    commands = ner.commands;

/* Initialize the extension. */
function init() {
  logger.info("Initializing service manager");
  var serviceManager = new services.NERServiceManager();
  
  logger.info("Initializing commands, changes, and operations");
  refineServlet.registerCommand(module, "services", new commands.ServicesCommand(serviceManager));
  refineServlet.registerCommand(module, "extractions", new commands.ExtractionCommand(serviceManager));
  refineServlet.registerClassMapping(
    "com.google.refine.model.changes.DataExtensionChange",
    "org.freeyourmetadata.ner.operations.NERChange"
  );
  refineServlet.cacheClass(Packages.org.freeyourmetadata.ner.operations.NERChange);
  operationRegistry.registerOperation(module, "ner", Packages.org.freeyourmetadata.ner.operations.NEROperation);

  logger.info("Initializing client resources");
  var resourceManager = Packages.com.google.refine.ClientSideResourceManager;
  resourceManager.addPaths(
    "project/scripts",
    module, [
      "scripts/config.js",
      "scripts/util.js",
      "dialogs/about.js",
      "dialogs/configuration.js",
      "dialogs/extraction.js",
      "scripts/menus.js",
    ]
  );
  resourceManager.addPaths(
    "project/styles",
    module, [
      "styles/main.less",
      "dialogs/dialogs.less",
      "dialogs/about.less",
      "dialogs/configuration.less",
      "dialogs/extraction.less",
    ]
  );
}

