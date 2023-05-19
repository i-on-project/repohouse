// @ts-check
import { test, expect } from '@playwright/test';
test('Login Created Teacher', async ({ page }) => {
    test.setTimeout(120000)
    await page.goto('http://localhost:3000/');
    await page.getByRole('button', { name: 'Open settings' }).click();
    const page1Promise = page.waitForEvent('popup');
    await page.getByRole('link', { name: 'Teacher' }).click();
    const page1 = await page1Promise;
    await page1.getByLabel('Username or email address').fill('i-on-classcode-teacher@outlook.pt');
    await page1.getByLabel('Password').fill('ClassCodeTeacher');
    await page1.getByRole('button', { name: 'Sign in' }).click();
    // Detects if the page is asking for authorization by identifying the authorize button
    try{
        if (await page1.getByRole('button', {name: 'Authorize Henriquess19'}).isVisible()){
            await page1.getByRole('button', {name: 'Authorize Henriquess19'}).click();
            await page1.waitForNavigation();
        }
    }catch (e) {
        console.log('No authorization needed');
    }
    await page.waitForLoadState('load');
    while (page.url() == 'http://localhost:3000/auth/teacher') {
        await page.waitForTimeout(500);
    }
    await expect(page.url()).toBe('http://localhost:3000/menu');
    console.log(await page.context().cookies());
});

test('Login Created Student', async ({ page }) => {
    test.setTimeout(120000)
    await page.goto('http://localhost:3000/');
    await page.getByRole('button', { name: 'Open settings' }).click();
    const page1Promise = page.waitForEvent('popup');
    await page.getByRole('link', { name: 'Student' }).click();
    const page1 = await page1Promise;
    await page1.getByLabel('Username or email address').fill('i-on-classcode-student@outlook.pt');
    await page1.getByLabel('Password').fill('ClassCodeStudent');
    await page1.getByRole('button', { name: 'Sign in' }).click();
    // Detects if the page is asking for authorization by identifying the authorize button
    try{
        if (await page1.getByRole('button', {name: 'Authorize Henriquess19'}).isVisible()){
            await page1.getByRole('button', {name: 'Authorize Henriquess19'}).click();
            await page1.waitForNavigation();
        }
    }catch (e) {
        console.log('No authorization needed');
    }
    await page.waitForLoadState('load');
    while (page.url() == 'http://localhost:3000/auth/student') {
        await page.waitForTimeout(500);
    }
    await expect(page.url()).toBe('http://localhost:3000/menu');
});

/**
test('Pending Teacher Page', async ({ page }) => {
    console.log('To be implemented');
});

test('Pending Student Page', async ({ page }) => {
    console.log('To be implemented');
});
**/