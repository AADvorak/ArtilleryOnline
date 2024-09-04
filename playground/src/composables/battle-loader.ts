import {useBattleStore} from "@/stores/battle";

export function useBattleLoader() {

  function loadBattle() {
    useBattleStore().battle = JSON.parse('{\n' +
        '    "model": {\n' +
        '        "shells": [],\n' +
        '        "room": {\n' +
        '            "specs": {\n' +
        '                "leftBottom": {\n' +
        '                    "x": 0.0,\n' +
        '                    "y": 0.1\n' +
        '                },\n' +
        '                "rightTop": {\n' +
        '                    "x": 16.0,\n' +
        '                    "y": 9.0\n' +
        '                },\n' +
        '                "step": 0.01,\n' +
        '                "gravityAcceleration": 9.8\n' +
        '            },\n' +
        '            "config": null,\n' +
        '            "state": null\n' +
        '        },\n' +
        '        "vehicles": {\n' +
        '            "Anton": {\n' +
        '                "specs": {\n' +
        '                    "hitPoints": 10.0,\n' +
        '                    "ammo": 30,\n' +
        '                    "minAngle": 0.0,\n' +
        '                    "maxAngle": 3.141592653589793,\n' +
        '                    "movingVelocity": 0.1,\n' +
        '                    "radius": 0.5,\n' +
        '                    "availableGuns": {\n' +
        '                        "default": {\n' +
        '                            "loadTime": 5.0,\n' +
        '                            "rotationVelocity": 0.1,\n' +
        '                            "availableShells": {\n' +
        '                                "default": {\n' +
        '                                    "velocity": 1.0,\n' +
        '                                    "damage": 1.0,\n' +
        '                                    "radius": 0.1\n' +
        '                                }\n' +
        '                            }\n' +
        '                        }\n' +
        '                    }\n' +
        '                },\n' +
        '                "config": {\n' +
        '                    "gun": {\n' +
        '                        "loadTime": 5.0,\n' +
        '                        "rotationVelocity": 0.1,\n' +
        '                        "availableShells": {\n' +
        '                            "default": {\n' +
        '                                "velocity": 1.0,\n' +
        '                                "damage": 1.0,\n' +
        '                                "radius": 0.1\n' +
        '                            }\n' +
        '                        }\n' +
        '                    },\n' +
        '                    "ammo": {\n' +
        '                        "default": 30\n' +
        '                    }\n' +
        '                },\n' +
        '                "state": {\n' +
        '                    "position": {\n' +
        '                        "x": 5.333333333333333,\n' +
        '                        "y": 0.0\n' +
        '                    },\n' +
        '                    "movingDirection": null,\n' +
        '                    "angle": 0.0,\n' +
        '                    "gunAngle": 1.0,\n' +
        '                    "gunRotatingDirection": null,\n' +
        '                    "hitPoints": 10.0,\n' +
        '                    "ammo": {\n' +
        '                        "default": 29\n' +
        '                    },\n' +
        '                    "gunState": {\n' +
        '                        "loadedShell": null,\n' +
        '                        "selectedShell": "default",\n' +
        '                        "loadingShell": "default",\n' +
        '                        "loadRemainTime": 0.500000000000001,\n' +
        '                        "triggerPushed": false\n' +
        '                    }\n' +
        '                },\n' +
        '                "id": 1\n' +
        '            },\n' +
        '            "Anton1": {\n' +
        '                "specs": {\n' +
        '                    "hitPoints": 10.0,\n' +
        '                    "ammo": 30,\n' +
        '                    "minAngle": 0.0,\n' +
        '                    "maxAngle": 3.141592653589793,\n' +
        '                    "movingVelocity": 0.1,\n' +
        '                    "radius": 0.5,\n' +
        '                    "availableGuns": {\n' +
        '                        "default": {\n' +
        '                            "loadTime": 5.0,\n' +
        '                            "rotationVelocity": 0.1,\n' +
        '                            "availableShells": {\n' +
        '                                "default": {\n' +
        '                                    "velocity": 1.0,\n' +
        '                                    "damage": 1.0,\n' +
        '                                    "radius": 0.1\n' +
        '                                }\n' +
        '                            }\n' +
        '                        }\n' +
        '                    }\n' +
        '                },\n' +
        '                "config": {\n' +
        '                    "gun": {\n' +
        '                        "loadTime": 5.0,\n' +
        '                        "rotationVelocity": 0.1,\n' +
        '                        "availableShells": {\n' +
        '                            "default": {\n' +
        '                                "velocity": 1.0,\n' +
        '                                "damage": 1.0,\n' +
        '                                "radius": 0.1\n' +
        '                            }\n' +
        '                        }\n' +
        '                    },\n' +
        '                    "ammo": {\n' +
        '                        "default": 30\n' +
        '                    }\n' +
        '                },\n' +
        '                "state": {\n' +
        '                    "position": {\n' +
        '                        "x": 10.666666666666666,\n' +
        '                        "y": 0.0\n' +
        '                    },\n' +
        '                    "movingDirection": null,\n' +
        '                    "angle": 0.0,\n' +
        '                    "gunAngle": 1.9,\n' +
        '                    "gunRotatingDirection": null,\n' +
        '                    "hitPoints": 10.0,\n' +
        '                    "ammo": {\n' +
        '                        "default": 29\n' +
        '                    },\n' +
        '                    "gunState": {\n' +
        '                        "loadedShell": null,\n' +
        '                        "selectedShell": "default",\n' +
        '                        "loadingShell": "default",\n' +
        '                        "loadRemainTime": 0.500000000000001,\n' +
        '                        "triggerPushed": false\n' +
        '                    }\n' +
        '                },\n' +
        '                "id": 2\n' +
        '            }\n' +
        '        }\n' +
        '    },\n' +
        '    "time": 4600,\n' +
        '    "battleStage": "ACTIVE"\n' +
        '}')
  }

  function startBattleLoading() {
    // todo loop
    loadBattle()
  }

  return { startBattleLoading }
}
