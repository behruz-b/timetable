+$ ->
  my.initAjax()

  Glob = window.Glob || {}

  apiUrl =
    getSubject: '/get-subjects'
    SortSubject: '/subjects'

  vm = ko.mapping.fromJS
    subjectList: []



  handleError = (error) ->
    if error.status is 500 or (error.status is 400 and error.responseText)
      toastr.error(error.responseText)
    else
      toastr.error('Something went wrong! Please try again.')

  getSubjectList = ->
    data =
      key: ""
    $.ajax
      url: apiUrl.SortSubject
      type: 'POST'
      data: JSON.stringify(data)
      dataType: 'json'
      contentType: 'application/json'
    .fail handleError
    .done (response) ->
      vm.subjectList(response)

  getSubjectList()

  vm.sortById = ->
    data =
      key: "id"
    $.ajax
      url: apiUrl.SortSubject
      type: 'POST'
      data: JSON.stringify(data)
      dataType: 'json'
      contentType: 'application/json'
    .fail handleError
    .done (response) ->
      vm.subjectList(response)

  vm.sortByName = ->
    data =
      key: "name"
    $.ajax
      url: apiUrl.SortSubject
      type: 'POST'
      data: JSON.stringify(data)
      dataType: 'json'
      contentType: 'application/json'
    .fail handleError
    .done (response) ->
      vm.subjectList(response)

  ko.applyBindings {vm}