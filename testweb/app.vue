<template>
  <div>
    <NuxtLayout>
      <NuxtPage />
    </NuxtLayout>
  </div>
</template>

<script lang="ts" setup>

// Server-side HTTP request
// const serverData = ref<any>(null)
// const { data } = await useFetch('https://jsonplaceholder.typicode.com/posts/1')
// serverData.value = data.value


// // Client-side HTTP request
// const clientData = ref<any>(null)

onMounted(async () => {
  // const response = await useFetch('http://192.168.5.211:8081/frontend/getallenablegamesort')
  // //const response = await fetch('https://jsonplaceholder.typicode.com/posts/2')
  // clientData.value = response.data
})

</script>