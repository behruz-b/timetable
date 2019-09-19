$ ->
  my.initAjax()

  Glob = window.Glob || {}

  apiUrl =
    send: '/get-rooms'

  vm = ko.mapping.fromJS
    selectedShift: ''
    studyShift: []
    selectedDay: ''
    weekDay: []
    selectedCouple: ''
    lCouple: []


  get = ->
    vm.studyShift([{id: 1, shift: "Morning"}, {id: 2, shift: "Afternoon"}])
    vm.weekDay([{id: 1, day: "Monday"}, {id: 2, day: "Tuesday"}, {id: 3, day: "Wednesday"}, {id: 4, day: "Thursday"},
      {id: 5, day: "Friday"}, {id: 6, day: "Saturday"}])
    vm.lCouple([{id: 1, couple: "couple 1"}, {id: 2, couple: "couple 2"}, {id: 3, couple: "couple 3"},
      {id: 4, couple: "coupl e4"}])
  get()


  handleError = (error) ->
    if error.status is 500 or (error.status is 400 and error.responseText)
      toastr.error(error.responseText)
    else
      toastr.error('Something went wrong! Please try again.')

  #  vm.onSubmit = ->
  #    toastr.clear()
  #    if (!vm.name())
  #      toastr.error("Please enter a subject name")
  #      return no
  #    else if (vm.name().length < 6)
  #      toastr.error("The subject name must consist of 6 letters")
  #      return no
  #    else if (!vm.selectedDirection())
  #      toastr.error("Please select group's direction")
  #      return no
  #
  #    data =
  #      name: vm.name()
  #      direction: vm.selectedDirection()
  #    $.ajax
  #      url: apiUrl.send
  #      type: 'POST'
  #      data: JSON.stringify(data)
  #      dataType: 'json'
  #      contentType: 'application/json'
  #    .fail handleError
  #    .done (response) ->
  #      toastr.success(response)


  ko.applyBindings {vm}