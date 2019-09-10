$ ->
  my.initAjax()

  Glob = window.Glob || {}

  apiUrl =
    send: '/send-subject'

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
      toastr.error("Please enter your name")
      return no
    else if (vm.name().length < 3)
      toastr.error("The login must consist of 3 letters")
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
      alert(response)

  ko.applyBindings {vm}