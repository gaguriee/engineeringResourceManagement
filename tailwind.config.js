module.exports = {

    plugins: [
        require('flowbite/plugin')({
            charts: true,
        }),
        // ... other plugins
    ],
    content: [
        "./node_modules/flowbite/**/*.js"
    ]


}