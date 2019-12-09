$ ->
  my.initAjax()

  Glob = window.Glob || {}

  apiUrl =
    getRooms: '/get-emptyrooms'


  vm = ko.mapping.fromJS
    listEmptyRooms: ''

  handleError = (error) ->
    if error.status is 500 or (error.status is 400 and error.responseText)
      toastr.error(error.responseText)
    else
      toastr.error('Something went wrong! Please try again.')

  getRoomsList = ->
    $('#wait').show();
    $('#capture-border').hide();
    $.ajax
      url: apiUrl.getRooms
      type: 'GET'
    .fail handleError
    .done (response) ->
      console.log(response)
      vm.listEmptyRooms(response)
      for room in response
        r = "#"+room
        $(r).removeClass("bg-danger").addClass("bg-success")
        $('#wait').hide();
        $('#capture-border').show();

  getRoomsList()


  ko.applyBindings {vm}