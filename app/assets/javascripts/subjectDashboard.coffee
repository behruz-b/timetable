$ ->
  my.initAjax()

  Glob = window.Glob || {}

  apiUrl =
    send: '/get-subjects'

  vm = ko.mapping.fromJS
    subjectList: []


  handleError = (error) ->
    if error.status is 500 or (error.status is 400 and error.responseText)
      toastr.error(error.responseText)
    else
      toastr.error('Something went wrong! Please try again.')

  getSubjectList = ->
    $.ajax
      url: apiUrl.send
      type: 'GET'
    .fail handleError
    .done (response) ->
      vm.subjectList(response)

  getSubjectList()

  ko.applyBindings {vm}