var NerExtension = {};

/* Menu handlers */
NerExtension.handlers = {
  about: function () {
    alert("Named-Entity Recognition Extension\n\nhttp://freeyourmetadata.org/");
  },
  configure: function () {
    alert("Configure Named-Entity Recognition Extension");
  },
  extractEntities: function (column) {
    alert("Extract entities in column " + column.name + ".");
  },
};

/* Add menu to extension bar */
ExtensionBar.addExtensionMenu({
  id: "named-entity-recognition",
  label: "Named-entity recognition",
  submenu: [
    {
      id   : "named-entity-recognition/configuration",
      label: "Configure API keys…",
      click: NerExtension.handlers.configure,
    },
    { /* separator */ },
    {
      id   : "named-entity-recognition/about",
      label: "About…",
      click: NerExtension.handlers.about,
    },
  ]
});

/* Add submenu to column header menu */
DataTableColumnHeaderUI.extendMenu(function (column, columnHeaderUI, menu) {
  MenuSystem.appendTo(menu, "", [
    { /* separator */ },
    {
      id: "named-entity-recognition/extract",
      label: "Extract named entities…",
      click: function () { NerExtension.handlers.extractEntities(column); },
    },
  ]);
});
