import {pathsToModuleNameMapper} from "ts-jest";

export default {
  preset: 'ts-jest/presets/default-esm',
  testEnvironment: 'node',
  extensionsToTreatAsEsm: ['.ts'],
  globals: {
    'ts-jest': {
      useESM: true,
      tsconfig: 'tsconfig.json'
    },
  },
  moduleNameMapper: pathsToModuleNameMapper({
    "~/*": ["*"],
    "@/*": ["*"]
  }, { prefix: '<rootDir>/' }),
};
