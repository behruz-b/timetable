$ ->
  my.initAjax()

  Glob = window.Glob || {}

  apiUrl =
    send: '/send-subject'

  vm = ko.mapping.fromJS
    inputText1: ''
    inputText2: ''

  handleError = (error) ->
    if error.status is 500 or (error.status is 400 and error.responseText)
      toastr.error(error.responseText)
    else
      toastr.error('Something went wrong! Please try again.')

  vm.onSubmit = ->
    toastr.clear()
    if (!vm.inputText1())
      toastr.error("Please input text first field")
      return no
    else if !vm.inputText2()
      toastr.error("Please input text second field")
      return no

    data =
      text1: vm.inputText1()
      text2: vm.inputText2()
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