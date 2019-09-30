$ ->
  my.initAjax()

  Glob = window.Glob || {}

  apiUrl =
    send: '/send-subject'
    getRooms: '/get-rooms'

  vm = ko.mapping.fromJS
    name: ''

  handleError = (error) ->
    if error.status is 500 or (error.status is 400 and error.responseText)
      toastr.error(error.responseText)
    else
      toastr.error('Something went wrong! Please try again.')

  vm.onSubmit = ->
    toastr.clear()
    if (!vm.name())
      toastr.error("Please enter a subject name")
      return no
    else if (vm.name().length < 6)
      toastr.error("The subject name must consist of 6 letters")
      return no

    data =
      name: vm.name()
    $.ajax
      url: apiUrl.send
      type: 'POST'
      data: JSON.stringify(data)
      dataType: 'json'
      contentType: 'application/json'
    .fail handleError
    .done (response) ->
      toastr.success(response)

  ko.applyBindings {vm}