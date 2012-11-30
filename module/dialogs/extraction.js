function ExtractionDialog(column) {
  this.column = column;
}

ExtractionDialog.prototype = {
  init: function (callback) {
    var self = this,
        selectedServices = {},
        dialogElement = this.dialogElement = $(DOM.loadHTML("named-entity-recognition", "dialogs/extraction.html"));
    
    /* Set labels */
    $('.column-name', dialogElement).text(this.column.name);
    
    /* Bind controls to actions */
    var controls = DOM.bind(this.dialogElement);
    controls.cancel.click(this.bound("hide"));
    
    /* Load service checkboxes */
    $.getJSON(NERExtension.servicesPath, function (services) {
      var $services = $(controls.services);
      services.forEach(function (service) {
        var $service = $('<input/>', {
                          type: 'checkbox',
                          change: function (event) {
                            selectedServices[service.name] = $(event.target).is(':checked');
                          }}),
            $label = $('<label/>', { 'class': 'checkbox' })
                        .append($service, service.name);
        $services.append($('<li/>').append($label));
      });
      if (callback)
        callback.apply(self);
    });
  },
  
  show: function () {
    this.init(function () {
      this.dialogLevel = DialogSystem.showDialog(this.dialogElement);
    });
  },
  
  hide: function () {
    DialogSystem.dismissUntil(this.dialogLevel - 1);
  },
};
