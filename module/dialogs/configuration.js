function ConfigurationDialog() {}

ConfigurationDialog.prototype = {
  init: function (callback) {
    var self = this;
    this.dialogElement = $(DOM.loadHTML("named-entity-recognition", "dialogs/configuration.html"));
    
    /* Bind controls to actions */
    var controls = DOM.bind(this.dialogElement);
    controls.cancel.click(this.bound("hide"));
    controls.update.click(this.bound("update"));
    
    /* Load service properties controls */
    $.getJSON(NERExtension.servicesPath, function (services) {
      var $services = $(controls.services);
      self.services = services;
      services.forEach(function (service) {
        var settings = service.settings,
            $title = $('<legend/>').text(service.name),
            $service = $('<fieldset/>').append($title),
            $settings = $('<ol>').appendTo($service),
            settingKeys = Object.keys(settings);
        // Add all settings
        if (settingKeys.length) {
          settingKeys.forEach(function (settingName) {
            var id = encodeURIComponent(service.name + '-' + settingName);
            $settings.append($('<li>')
                .append($('<label>', { 'for': id, text: settingName }),
                        $('<input>', { id: id, value: settings[settingName],
                                       change: function (event) { settings[settingName] = $(event.target).val(); },
                                     })));
          });
        }
        else {
          $settings.append($('<li/>').append($('<em/>').text('This service requires no configuration.')))
        }

        // Add configuration instructions
        if (service.documentation)
          $title.append(' ', $('<a/>', { 'class': 'documentation',
                                         text: 'get a free API key',
                                         href: service.documentation,
                                         target: '_blank' }));
        // Add the service to the list
        $services.append($service);
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
  
  update: function () {
    $.ajax({
      url: NERExtension.servicesPath,
      type: "PUT",
      data: JSON.stringify(this.services),
      success: this.bound("hide"),
    });
  },
};
