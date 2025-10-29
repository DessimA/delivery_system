<template>
  <BaseModal :visible="visible" @close="onClose" :title="modalTitle">
    <form @submit.prevent="onSubmit" class="product-form">
      <div class="form-group">
        <label for="nomeProduto">Nome do Produto</label>
        <BaseInput id="nomeProduto" v-model="formData.nomeProduto" placeholder="Ex: Pizza de Calabresa" required />
      </div>
      <div class="form-group">
        <label for="descricao">Descrição</label>
        <textarea id="descricao" v-model="formData.descricao" class="form-control" placeholder="Ex: Molho de tomate, queijo, calabresa e orégano" required></textarea>
      </div>
      <div class="form-group">
        <label for="preco">Preço</label>
        <BaseInput id="preco" v-model.number="formData.preco" type="number" step="0.01" placeholder="Ex: 35.50" required />
      </div>
      <div class="form-group">
        <label for="caminhoImagem">URL da Imagem</label>
        <BaseInput id="caminhoImagem" v-model="formData.caminhoImagem" placeholder="Ex: https://example.com/imagem.png" />
      </div>
    </form>
    <template #footer>
      <BaseButton variant="secondary" label="Cancelar" @click="onClose" />
      <BaseButton variant="primary" label="Salvar" @click="onSubmit" :loading="saving" />
    </template>
  </BaseModal>
</template>

<script setup>
import { ref, watch, computed } from 'vue';
import BaseModal from '@/components/base/BaseModal.vue';
import BaseInput from '@/components/base/BaseInput.vue';
import BaseButton from '@/components/base/BaseButton.vue';

const props = defineProps({
  visible: Boolean,
  product: Object, // Null for create, object for edit
  saving: Boolean,
});

const emit = defineEmits(['close', 'save']);

const formData = ref({});

const modalTitle = computed(() => props.product ? 'Editar Produto' : 'Adicionar Novo Produto');

// Watch for prop changes to populate the form
watch(() => props.product, (newProduct) => {
  if (newProduct) {
    formData.value = { ...newProduct };
  } else {
    // Reset for new product
    formData.value = {
      nomeProduto: '',
      descricao: '',
      preco: null,
      caminhoImagem: '',
    };
  }
}, { immediate: true });

const onClose = () => {
  emit('close');
};

const onSubmit = () => {
  // Basic validation
  if (formData.value.nomeProduto && formData.value.preco) {
    emit('save', formData.value);
  }
};
</script>

<style lang="scss" scoped>
.product-form {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);

  label {
    font-weight: var(--font-weight-semibold);
  }
}

// Basic styling for textarea to match BaseInput
.form-control {
    width: 100%;
    padding: var(--spacing-sm) var(--spacing-md);
    border: 1px solid var(--color-border);
    border-radius: var(--radius-md);
    background-color: var(--color-surface);
    color: var(--color-text-dark);
    transition: border-color var(--transition-fast);

    &:focus {
        outline: none;
        border-color: var(--color-primary);
    }
}

textarea.form-control {
    min-height: 100px;
    resize: vertical;
}
</style>
