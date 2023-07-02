module.exports = {
  env: {
    browser: true,
    es2021: true
  },
  extends: [
    'eslint:recommended',
    'plugin:react/recommended',
    'airbnb',
    'prettier',
    'plugin:@typescript-eslint/recommended',
    'plugin:import/typescript'
  ],
  overrides: [
    {
      files: ['**/__tests__/*.js', '**/*.test.js', '**/*.spec.js'],
      env: {
        jest: true
      }
    }
  ],
  parser: '@typescript-eslint/parser',
  parserOptions: {
    ecmaVersion: 'latest',
    sourceType: 'module'
  },
  plugins: ['react', 'react-hooks', 'prettier', 'jest', 'import'],
  rules: {
    'no-alert': 'off', // TODO: Drop this later.
    'no-use-before-define': 'off', // TODO: Drop this later.
    'no-empty-function': 'off', // TODO: Drop this later.
    'no-console': 'warn',
    'no-trailing-spaces': 'error',
    'comma-dangle': ['error', 'never'],
    'default-param-last': 'off',
    'no-unused-vars': 'warn', // TODO: Drop this later.
    'react/react-in-jsx-scope': 'off',
    'react/jsx-filename-extension': 'off',
    'react/destructuring-assignment': 'off',
    'react/jsx-props-no-spreading': 'off',
    'react-hooks/rules-of-hooks': 'error',
    'react/function-component-definition': 'off',
    'import/no-extraneous-dependencies': 'error',
    'import/extensions': 'off',
    'import/prefer-default-export': 'off', // TODO: Drop this later..
    'consistent-return': 'off', // TODO: Drop this later.
    'semi': ['error', 'never'],
    // 'react/jsx-handler-names': [
    //   'error',
    //   {
    //     eventHandlerPrefix: 'handle',
    //     eventHandlerPropPrefix: 'on'
    //   }
    // ],
    'react/jsx-handler-names': 'off',
    'import/order': [
      'error',
      {
        'groups': ['builtin', 'external', 'internal'],
        'pathGroups': [
          {
            pattern: 'react',
            group: 'external',
            position: 'before'
          }
        ],
        'pathGroupsExcludedImportTypes': ['react'],
        'newlines-between': 'always',
        'alphabetize': {
          order: 'asc',
          caseInsensitive: true
        }
      }
    ],
    'react/no-array-index-key': 'off', // TODO: Drop this later.
    'no-nested-ternary': 'off', // TODO: Drop this later.
    'import/no-cycle': 'off', // TODO: Drop this later.
    'react/prop-types': 'off', // TODO: remove this line after converting all pages to typescript.
    'arrow-parens': ['error', 'always'],
    'quotes': ['error', 'single'],
    'no-plusplus': 'off',
    'react/jsx-no-useless-fragment': 'off', // TODO: Drop this later.
    'no-case-declarations': 'off', // TODO: Drop this later.
    'class-methods-use-this': 'off', // TODO: Drop this later.
    '@typescript-eslint/no-empty-function': 'off', // TODO: Drop this later.
    'jsx-a11y/click-events-have-key-events': 'off', // TODO: Drop this later.
    'jsx-a11y/no-static-element-interactions': 'off', // TODO: Drop this later.
    // Rule override for test files
    'jest/no-undef': 'off',
    // Errors converted to warnings
    'camelcase': 'warn',
    'no-shadow': 'warn',
    '@typescript-eslint/no-explicit-any': 'warn',
    'no-unused-expressions': 'warn',
    'prefer-destructuring': 'warn',
    'no-unsafe-optional-chaining': 'warn',
    'no-param-reassign': 'warn',
    'react/button-has-type': 'warn',
    'jsx-a11y/no-noninteractive-element-interactions': 'warn',
    'jsx-a11y/label-has-associated-control': 'warn',
    'jsx-a11y/control-has-associated-label': 'warn',
    'react/no-children-prop': 'warn',
    'react/no-unstable-nested-components': 'warn',
    'no-restricted-globals': 'warn'
  }
}
