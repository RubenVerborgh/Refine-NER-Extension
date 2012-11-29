function ExtractionDialog(column) {
  this.column = column;
}

ExtractionDialog.prototype = {
  init: function () {
    var dialogElement = this.dialogElement = $(DOM.loadHTML("named-entity-recognition", "dialogs/extraction.html"));
    
    /* Set labels */
    $('.column-name', dialogElement).text(this.column.name);
    
    /* Bind controls */
    var controls = DOM.bind(this.dialogElement);
    controls.cancel.click(this.bound("hide"));
  },
  
  show: function () {
    this.init();
    this.dialogLevel = DialogSystem.showDialog(this.dialogElement);
  },
  
  hide: function () {
    DialogSystem.dismissUntil(this.dialogLevel - 1);
  },
};
