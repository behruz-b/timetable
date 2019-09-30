$ ->
  my.initAjax()

  Glob = window.Glob || {}

  apiUrl =
    send: '/loginPost'

  vm = ko.mapping.fromJS
    login: ''
    pass: ''

  handleError = (error) ->
    if error.status is 500 or (error.status is 400 and error.responseText)
      toastr.error(error.responseText)
    else
      toastr.error('Something went wrong! Please try again.')

  redirect = () ->
    window.location.replace("https://dars-jadvali-ubtuit.herokuapp.com/`")

  vm.onSubmit = ->
    toastr.clear()
    if (!vm.login())
      toastr.error("Please enter login")
      return no
    else if (vm.login().length < 3)
      toastr.error("The login must consist of 3 letters")
      return no
    else if (!vm.pass())
      toastr.error("Please enter password")
      return no
    else if (vm.pass().length < 6)
      toastr.error("The password must consist of 3 letters")
      return no

    data =
      login: vm.login()
      password: vm.pass()

    $.ajax
      url: apiUrl.send
      type: 'POST'
      data: JSON.stringify(data)
      dataType: 'json'
      contentType: 'application/json'
    .fail redirect
    .done (response) ->
      toastr.error(response)


  ko.applyBindings {vm}