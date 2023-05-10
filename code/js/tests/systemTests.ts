// @ts-check
import { test, expect } from '@playwright/test';

test('first test', async ({ page }) => {
    await page.goto('http://localhost:3000/');
    await expect(page.getByRole('heading', { name: 'i-on ClassCode' })).toBeVisible();
});

test('credits Page', async ({ page }) => {
    await page.goto('http://localhost:3000/');
    await page.getByRole('heading', { name: 'i-on ClassCode' }).click();
    await page.getByRole('link', { name: 'Credits' }).click();
    await expect(page.getByRole('heading', { name: 'Students' })).toBeVisible();
    await expect(page.getByRole('heading', { name: 'Professor' })).toBeVisible();
});

test('student login', async ({ page }) => {
    await page.goto('http://localhost:3000/');
    await page.getByRole('button', { name: 'Open settings' }).click();
    await expect(page.getByRole('link', { name: 'Student' })).toBeVisible();
});


test('teacher login', async ({ page }) => {
    await page.goto('http://localhost:3000/');
    await page.getByRole('button', { name: 'Open settings' }).click();
    await expect(page.getByRole('link', { name: 'Teacher' })).toBeVisible();
});

test('go back to homepage through title', async ({ page }) => {
    await page.goto('http://localhost:3000/');
    await page.getByRole('button', {name: 'Credits'}).click();
    await page.getByText('ClassCode').first().click();
    await expect(page.getByRole('heading', { name: 'ClassCode' })).toBeVisible();
});
