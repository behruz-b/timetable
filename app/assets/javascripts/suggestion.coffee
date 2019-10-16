$ ->
  my.initAjax()

  Glob = window.Glob || {}

  apiUrl =
    send: '/suggestionPost'
    getSuggestion: '/get-suggestion'

  vm = ko.mapping.fromJS
    teacherName: ''
    suggestion: ''
    suggestionList: []

  handleError = (error) ->
    if error.status is 500 or (error.status is 400 and error.responseText)
      toastr.error(error.responseText)
    else
      toastr.error('Something went wrong! Please try again.')

  vm.onSubmit = ->
    toastr.clear()
    if (!vm.teacherName())
      toastr.error("Please enter a teacher's name")
      return no
    else if (vm.teacherName().length < 3)
      toastr.error("The teacher's name must consist of 3 letters")
      return no
    else if (!vm.suggestion())
      toastr.error("Please enter suggestion")
      return no

    data =
      teacherName: vm.teacherName()
      suggestion: vm.suggestion()
    $.ajax
      url: apiUrl.send
      type: 'POST'
      data: JSON.stringify(data)
      dataType: 'json'
      contentType: 'application/json'
    .fail handleError
    .done (response) ->
      toastr.success(response)


  getSuggestionList = ->
    data =
      key: ""
    $.ajax
      url: apiUrl.getSuggestion
      type: 'POST'
      data: JSON.stringify(data)
      dataType: 'json'
      contentType: 'application/json'
    .fail handleError
    .done (response) ->
      vm.suggestionList(response)

  getSuggestionList()

  vm.sortById = ->
    data =
      key: "id"
    $.ajax
      url: apiUrl.getSuggestion
      type: 'POST'
      data: JSON.stringify(data)
      dataType: 'json'
      contentType: 'application/json'
    .fail handleError
    .done (response) ->
      vm.suggestionList(response)

  vm.sortByTeacherName = ->
    data =
      key: "teacherName"
    $.ajax
      url: apiUrl.getSuggestion
      type: 'POST'
      data: JSON.stringify(data)
      dataType: 'json'
      contentType: 'application/json'
    .fail handleError
    .done (response) ->
      vm.suggestionList(response)


  ko.applyBindings {vm}