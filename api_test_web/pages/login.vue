<template>
    <div class="w-full flex flex-col justify-center items-center pt-3">
        <div class="w-50 h-full flex flex-col justify-center items-center">
            <UFormField class="m-0.5" label="Email">
                <UInput placeholder="insert email" v-model="username" />
            </UFormField>
            <UFormField class="m-0.5" label="Password">
                <UInput type="password" placeholder="insert password" v-model="password" />
            </UFormField>
        </div>
        <USeparator class="w-full m-0.5" />
        <div class="w-50 h-full flex flex-col justify-center items-center">
            
        </div>
    </div>

</template>

<script setup>
const username = ref()
const password = ref()
</script>