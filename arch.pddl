(define (domain arch)

  (:requirements :strips :typing)

  (:types component connector interface - object
		  port role - interface
  )

  (:predicates 	(connect ?con - connector ?com1 ?com2  - component)
				(hasport ?com - component ?prt - port)
				(hasrole ?con - connector ?rle - role)
				(attach ?com - component ?prt - port ?con - connector ?rle - role)
        (enabled-component ?com - component)
        (disabled-component ?com - component)
        (enabled-connector ?con - connector)
        (disabled-connector ?con - connector)
  )

  (:action setup-component
    :parameters (?com - component)
    :precondition (disabled-component ?com)
    :effect (enabled-component ?com))

  (:action remove-component
    :parameters (?com - component)
    :precondition (enabled-component ?com)
    :effect (disabled-component ?com))

  (:action setup-connector
      :parameters (?con - connector)
      :precondition (disabled-connector ?con)
      :effect (enabled-connector ?con))

  (:action remove-connector
      :parameters (?con - connector)
      :precondition (enabled-connector ?con)
      :effect (disabled-connector ?con))

  (:action establish-link
    :parameters (?com - component ?prt - port ?con - connector ?rle - role)
    :precondition (and (hasport ?com  ?prt)  (hasrole ?con  ?rle) (enabled-component ?com) (enabled-connector ?con))
    :effect (attach ?com ?prt ?con ?rle))


)
